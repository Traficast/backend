package com.traficast.config;


import com.zaxxer.hikari.util.ClockSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
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
        return new RestTemplate(clientHttpRequestFactory());
    }

    /**
     * 빠른 응답이 필요한 내부 API용 RestTemplate
     */
    @Bean("fastRestTemplate")
    public RestTemplate fastRestTemplate(RestTemplateBuilder builder){
        return new RestTemplate(fastClientHttpRequestFactory());
    }

    /**
     * 일반 요청용 Http 클라이언트 팩토리
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(60));
        return factory;
    }

    /**
     * 빠른 응답용 HTTP 클라이언트 팩토리
     */
    @Bean
    public ClientHttpRequestFactory fastClientHttpRequestFactory(){
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(3));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }
}
