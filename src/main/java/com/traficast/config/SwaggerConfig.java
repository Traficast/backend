package com.traficast.config;


import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * Swagger/OpenAPI 3.0 문서화 설정
 * API 문서 자동 생성 및 테스트 UI 생성
 */

@Configuration
@Slf4j
public class SwaggerConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * OpenAPI 3.0 설정
     */
    @Bean
    public OpenAPI customOpenAPI(){
        log.info("Swagger OpenApI 설정 초기화");

        return new OpenAPI()
                .info(new Info()
                        .title("교통량 예측 시스템 API")
                        .version("1.0.0")
                        .description("""
                                LSTM 기반 교통량 예측 시스템의 Restful API 문서입니다.
                                
                                ### 주요 기능
                                - 교통량 예측 요청 및 결과 조회
                                - 위치 정보 관리 (CRUD)
                                - 교통 데이터 업로드
                                - 시스템 상태 모니터링
                                
                                ## 인증
                                현재 개발 단계로 인증이 비활성화되어 있습니다.
                                """)
                        .contact(new Contact()
                                .name("Traffic Prediction Team")
                                .email("admin@traficast.com")
                                .url("https://traficast.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost: " + serverPort)
                                .description("개발 서버"),
                        new Server()
                                .url("http://api.traficast.com")
                                .description("운영 서버")
                ))
                .components(new Components());
    }
}
