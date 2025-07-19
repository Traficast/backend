package com.traficast.config;


import com.zaxxer.hikari.util.ClockSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;


/**
 * 외부 API 통신을 위한 RestTemplate 설정
 * ML 모델 API와의 통신 최적화를 위한 타임아웃 및 커넥션 풀 설정
 */
@Configuration
@Slf4j
public class RestTemplateConfig {

    /**
     * 외부 ML 모델 API 통신용 RestTemplate
     * 예측 요청은 시간이 오래 걸릴 수 있으므로 타임아웃을 길게 설정
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder){
        log.info("RestTemplate 설정 초기화");

        // Apache HttpComponents 사용하여 연결 풀링 최적화
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(10000); // 연결 타임아웃 10초
        factory.setReadTimeout(60000); // 읽기 타임아웃 60초

        return builder
                .setConnectTimeout(Duration.ofSeconds(10))
                .setReadTimeout(Duration.ofSeconds(60))
                .requestFactory(() -> factory)
                .build();
    }

    /**
     * 빠른 응답이 필요한 내부 API용 RestTemplate
     */
    @Bean("fastRestTemplate")
    public RestTemplate fastRestTemplate(RestTemplateBuilder builder){
        return builder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(10))
                .build();
    }
}
