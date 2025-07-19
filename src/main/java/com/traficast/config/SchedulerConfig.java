package com.traficast.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;


/**
 * 스케줄링 작업을 위한 설정 클래스
 * 일일/시간별 예측, 정확도 검증 등 주기적 작업을 위한
 */
@Configuration
@EnableScheduling
@Slf4j
public class SchedulerConfig {

    /**
     * 스케줄링 작업용 전용 스레드 풀
     * 예측 작업이 메인 애플리케이션 성능에 영향을 주지 않도록 분리
     */
    @Bean
    public TaskScheduler taskScheduler() {
        log.info("스케줄러 스레드 풀 설정 초기화");

        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(5); // 동시 실행 가능한 스케줄 작업 수
        scheduler.setThreadNamePrefix("traffic-scheduler-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setRejectedExecutionHandler((r, executor) -> {
            log.warn("스케줄 작업 거부됨: {}", r.toString());
        });

        return scheduler;
    }

    /**
     * 비동기 작업용 스레드 풀
     * 대용량 데이터 처리나 시간이 오래 걸리는 작업용
     */
    @Bean("asyncTaskExecutor")
    public ThreadPoolTaskScheduler asyncTaskExecutor(){
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(10);
        executor.setThreadNamePrefix("async-task-");
        executor.setAwaitTerminationSeconds(30);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        return executor;
    }
}
