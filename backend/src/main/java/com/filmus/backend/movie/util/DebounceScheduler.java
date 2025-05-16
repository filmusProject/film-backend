package com.filmus.backend.movie.util;

import com.filmus.backend.movie.external.NlpEc2Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


@ConditionalOnProperty(name = "aws.enabled", havingValue = "true")
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class DebounceScheduler {

    private final NlpEc2Manager ec2Manager;
    private volatile Instant lastAccess = Instant.EPOCH;  // 마지막 API 호출 시각

    /* ---------- API 호출 시점마다 실행 ---------- */
    public void markUsed() {
        lastAccess = Instant.now();
    }

    /* ---------- 5분마다 유휴 검사 ---------- */
    @Scheduled(fixedDelay = 300_000)   // 5 분
    public void shutdownIfIdle() {
        if (lastAccess.plus(ec2Manager.stopDelayMin(), ChronoUnit.MINUTES)
                .isBefore(Instant.now())) {
            ec2Manager.stopIfIdle();
            lastAccess = Instant.EPOCH;   // 초기화
        }
    }
}