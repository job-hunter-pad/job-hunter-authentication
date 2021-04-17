package backend.service.profile.controller;

import backend.service.profile.controller.dto.ProfileDTO;
import backend.service.profile.model.Review;
import backend.service.profile.model.UserProfile;
import backend.service.profile.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class UserProfileController {
    private final ProfileService profileService;

    @Autowired
    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/profile/{userId}")
    UserProfile getProfile(@PathVariable String userId) {
        return profileService.getProfile(userId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/profile/all")
    List<UserProfile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    @PostMapping("/profile/{userId}/addReview")
    UserProfile addReviewToProfile(@PathVariable String userId, @RequestBody Review review) {
        try {
            return profileService.addReview(userId, review);
        } catch (NullPointerException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/profile/{userId}/update")
    UserProfile updateProfile(@PathVariable String userId, @RequestBody ProfileDTO profileDTO) {
        UserProfile newUserProfile = new UserProfile(userId, profileDTO.getName(), null);

        newUserProfile.setName(profileDTO.getName());
        newUserProfile.setDescription(profileDTO.getDescription());
        newUserProfile.setLocation(profileDTO.getLocation());
        newUserProfile.setPhoneNumber(profileDTO.getPhoneNumber());

        return profileService.updateProfile(newUserProfile).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
