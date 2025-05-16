package com.filmus.backend.movie.external;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.InstanceStateName;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;


@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
@Slf4j
@Component
@RequiredArgsConstructor
public class NlpEc2Manager {

    private final Ec2Props props;                 // ← 주입
    private final Ec2Client ec2 = Ec2Client.create();

    private final ReentrantLock stateLock = new ReentrantLock();
    private CompletableFuture<Void> bootFuture;

    /* ───────── NLP 인스턴스 ON ───────── */
    public void ensureRunning() {
        stateLock.lock();
        try {
            if (isRunning()) return;
            if (bootFuture == null || bootFuture.isCompletedExceptionally()) {
                bootFuture = CompletableFuture.runAsync(this::startInstance);
            }
        } finally { stateLock.unlock(); }
        bootFuture.join();
    }

    private void startInstance() {
        log.info("NLP EC2 기동 요청");
        ec2.startInstances(r -> r.instanceIds(props.id()));
        Instant deadline = Instant.now().plusSeconds(props.startTimeoutSec());
        while (Instant.now().isBefore(deadline)) {
            if (currentState() == InstanceStateName.RUNNING) {
                log.info("NLP EC2 RUNNING 확인");
                return;
            }
            try { Thread.sleep(5_000); } catch (InterruptedException ignored) {}
        }
        throw new IllegalStateException("NLP EC2 부팅 타임아웃");
    }

    /* ───────── NLP 인스턴스 OFF ───────── */
    public void stopIfIdle() {
        if (isRunning()) {
            log.info("NLP EC2 STOP 시도");
            ec2.stopInstances(r -> r.instanceIds(props.id()));
        }
    }

    /* 외부에 필요한 값만 노출 */
    public int stopDelayMin() { return props.stopDelayMin(); }

    /* 헬퍼 */
    private boolean isRunning() {
        return currentState() == InstanceStateName.RUNNING;
    }
    private InstanceStateName currentState() {
        return ec2.describeInstances(r -> r.instanceIds(props.id()))
                .reservations().get(0).instances().get(0).state().name();
    }

    /* ───────── 설정 바인딩용 record ───────── */
    @ConfigurationProperties(prefix = "aws")          // ← 이것만 있으면 충분
    public static record Ec2Props(
            String nlpInstanceId,
            int startTimeoutSec,
            int stopDelayMin
    ) {
        public String id() { return nlpInstanceId; }
    }
}