package backend.service.profile.repository;

import backend.service.profile.model.UserProfilePhoto;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProfilePhotoRepository extends MongoRepository<UserProfilePhoto, String> {
}
