package com.traficast.config;


import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 *  데이터베이스 관련 설정 클래스
 *  JPA Auditing, Repository 스캔, 트랜잭션 관리를 활성화
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.traficast.repository")
@EnableJpaAuditing // BaseEntity의 createdAt, updatedAt 자동 관리
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    public DatabaseConfig(){
        log.info("데이터베이스 설정 초기화 완료");
    }
}
