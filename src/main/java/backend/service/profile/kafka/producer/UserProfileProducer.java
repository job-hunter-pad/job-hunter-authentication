package backend.service.profile.kafka.producer;

import backend.service.profile.model.UserProfile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserProfileProducer {

    private final KafkaTemplate<String, UserProfile> jobApplicationsKafkaTemplate;

    private static final String TOPIC = "profile";

    public UserProfileProducer(KafkaTemplate<String, UserProfile> jobApplicationsKafkaTemplate) {
        this.jobApplicationsKafkaTemplate = jobApplicationsKafkaTemplate;
    }

    public String postUserProfile(UserProfile userProfile) {
        jobApplicationsKafkaTemplate.send(TOPIC, userProfile);
        return "Published successfully";
    }
}
