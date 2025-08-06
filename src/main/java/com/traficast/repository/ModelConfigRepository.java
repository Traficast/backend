package com.traficast.repository;

import com.traficast.entity.ModelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModelConfigRepository extends JpaRepository<ModelConfig, Long> {

    /**
     * 현재 활성화된 모델 조회(최신 생성된 것부터)
     */
    Optional<ModelConfig> findTopByIsActiveTrueAndIsDeletedFalseOrderByActivationDateDesc();

    /**
     * 모델 타입별 조회
     */
    List<ModelConfig> findByModelTypeAndIsDeletedFalseOrderByCreatedAtDesc(ModelConfig.ModelType modelType);

    /**
     * 활성화된 모든 모델 조회
     */
    List<ModelConfig> findByIsActiveTrueAndIsDeletedFalseOrderByCreatedAtDesc();
}
