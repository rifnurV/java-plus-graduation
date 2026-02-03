package ru.practicum.stats.service.aggregator.configuration;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@Component
@ConfigurationProperties(prefix = "collector.kafka.producer.topics")
public class KafkaConfig {

    @NotNull(message = "eventsTopic не может быть пустым")
    private String eventsTopic;
}
