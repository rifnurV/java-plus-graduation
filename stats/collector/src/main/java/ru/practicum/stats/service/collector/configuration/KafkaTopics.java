package ru.practicum.stats.service.collector.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "collector.kafka.producer.topics")
@Validated
public class KafkaTopics {

    @NotBlank(message = "userActions не может быть пустым")
    String userActions;

}
