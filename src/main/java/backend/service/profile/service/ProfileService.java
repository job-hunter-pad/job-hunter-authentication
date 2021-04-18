package backend.service.profile.service;

import backend.service.authentication.model.User;
import backend.service.profile.model.Review;
import backend.service.profile.model.UserProfile;
import backend.service.profile.model.UserProfilePhoto;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface ProfileService {
    Optional<UserProfile> getProfile(String userId);

    List<UserProfile> getAllProfiles();

    UserProfile createProfile(User user);

    Optional<UserProfile> updateProfile(UserProfile userProfile);

    UserProfile addReview(String userId, Review review);

    Optional<UserProfilePhoto> getUserProfilePhoto(String userId);

    UserProfilePhoto updateProfilePhoto(UserProfilePhoto userProfilePhoto);
}
