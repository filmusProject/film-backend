package com.filmus.backend.recommend.fixed.scheduler;

import com.filmus.backend.recommend.fixed.service.FixedRecommendationService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FixedRecommendationScheduler {

    private final FixedRecommendationService recommendService;



    // 매일 자정 00:00:00에 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void runRecommendationJob() {
        log.info("[추천 스케줄러] 오늘의 추천을 생성합니다.");
        recommendService.saveDailyRecommendations();
    }
}
