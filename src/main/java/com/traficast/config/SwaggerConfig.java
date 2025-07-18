package com.traficast.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("교통량 예측 시스템 API")
                        .version("1.0.0")
                        .description("LSTM 기반 교통량 예측 시스템의 RESTful API 문서")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }
}
