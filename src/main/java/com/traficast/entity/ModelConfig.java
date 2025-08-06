package com.traficast.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="model_configs", indexes = {
        @Index(name="idx_model_version", columnList = "model_version"),
        @Index(name="idx_is_active", columnList = "is_active")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelConfig extends BaseEntity{

    @Column(name = "model_name", nullable = false, length = 100)
    private String modelName;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    @Column(name = "model_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelType modelType;

    @Column(name= "api_endpoint", length = 200)
    private String apiEndpoint;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = false;

    // 모델 타입 열거형
    public enum ModelType{
        T_GCN("Temoral Graph Convolutional Network"),
        LSTM("Long Short-Term Memory"),
        ARIMA("AutoRegressive Integrated Moving Average"),
        PROPHET("Facebook Prophet");

        private final String description;

        ModelType(String description){
            this.description = description;
        }

        public String getDescription(){
            return description;
        }
    }

}
