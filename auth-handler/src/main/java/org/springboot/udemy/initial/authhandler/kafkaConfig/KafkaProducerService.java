package org.springboot.udemy.initial.authhandler.kafkaConfig;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic.user-created}")
    private String topic;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        kafkaTemplate.send(topic, event.getUserId(), event);
        System.out.println("🔊 Published user-created event for userId: " + event.getUserId());
    }
}
