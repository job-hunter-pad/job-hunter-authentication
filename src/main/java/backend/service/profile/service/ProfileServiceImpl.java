package backend.service.profile.service;

import backend.service.authentication.model.User;
import backend.service.profile.model.Review;
import backend.service.profile.model.UserProfile;
import backend.service.profile.model.UserProfilePhoto;
import backend.service.profile.repository.ProfilePhotoRepository;
import backend.service.profile.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final ProfilePhotoRepository profilePhotoRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository, ProfilePhotoRepository profilePhotoRepository) {
        this.profileRepository = profileRepository;
        this.profilePhotoRepository = profilePhotoRepository;
    }

    @Override
    public Optional<UserProfile> getProfile(String userId) {
        return profileRepository.findById(userId);
    }

    @Override
    public List<UserProfile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Override
    public UserProfile createProfile(User user) {
        return profileRepository.save(new UserProfile(user.getId(), user.getEmail(), user.getName(), user.getUserType()));
    }

    @Override
    public Optional<UserProfile> updateProfile(UserProfile userProfile) {
        if (userProfile == null) {
            return Optional.empty();
        }

        Optional<UserProfile> optionalProfile = getProfile(userProfile.getUserId());

        if (optionalProfile.isPresent()) {
            UserProfile storedUserProfile = optionalProfile.get();

            if (userProfile.getLocation() != null) {
                storedUserProfile.setLocation(userProfile.getLocation());
            }
            if (userProfile.getDescription() != null) {
                storedUserProfile.setDescription(userProfile.getDescription());
            }
            if (userProfile.getName() != null) {
                storedUserProfile.setName(userProfile.getName());
            }
            if (userProfile.getPhoneNumber() != null) {
                storedUserProfile.setPhoneNumber(userProfile.getPhoneNumber());
            }
            if (userProfile.getSkills() != null) {
                storedUserProfile.setSkills(userProfile.getSkills());
            }

            optionalProfile = Optional.of(profileRepository.save(storedUserProfile));
        }

        return optionalProfile;
    }

    @Override
    public UserProfile addReview(String userId, Review review) {
        Optional<UserProfile> optionalProfile = getProfile(userId);
        if (optionalProfile.isPresent()) {
            UserProfile userProfile = optionalProfile.get();
            userProfile.addReview(review);
            return profileRepository.save(userProfile);
        }

        throw new NullPointerException();
    }

    @Override
    public Optional<UserProfilePhoto> getUserProfilePhoto(String userId) {
        return profilePhotoRepository.findById(userId);
    }

    @Override
    public UserProfilePhoto updateProfilePhoto(UserProfilePhoto userProfilePhoto) {
        return profilePhotoRepository.save(userProfilePhoto);
    }
}
