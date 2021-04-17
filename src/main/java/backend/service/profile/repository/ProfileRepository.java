package backend.service.profile.repository;


import backend.service.profile.model.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfileRepository extends MongoRepository<UserProfile, String> {
}
