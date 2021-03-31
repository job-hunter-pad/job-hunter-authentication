package backend.service.authentication.kafka.producer;

import backend.service.authentication.kafka.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @Autowired
    private KafkaTemplate<String, Email> kafkaTemplate;

    private static final String TOPIC = "email";

    public String postEmail(Email email) {
        kafkaTemplate.send(TOPIC, email);
        return "Published successfully";
    }

}
