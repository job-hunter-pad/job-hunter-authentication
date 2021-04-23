package backend.service.profile.controller;

import backend.service.profile.controller.dto.ProfileDTO;
import backend.service.profile.model.Review;
import backend.service.profile.model.UserProfile;
import backend.service.profile.model.UserProfilePhoto;
import backend.service.profile.service.ProfileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
public class UserProfileController {
    private final ProfileService profileService;

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
        UserProfile newUserProfile = new UserProfile();

        newUserProfile.setUserId(userId);
        newUserProfile.setName(profileDTO.getName());
        newUserProfile.setDescription(profileDTO.getDescription());
        newUserProfile.setLocation(profileDTO.getLocation());
        newUserProfile.setPhoneNumber(profileDTO.getPhoneNumber());
        newUserProfile.setSkills(profileDTO.getSkills());

        return profileService.updateProfile(newUserProfile).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/profile/{userId}/getPhoto")
    @ResponseBody
    ResponseEntity<byte[]> getProfilePhoto(@PathVariable String userId) {
        UserProfilePhoto userProfilePhoto = profileService.getUserProfilePhoto(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", userProfilePhoto.getContentType());

        return new ResponseEntity<>(userProfilePhoto.getProfilePhotoBytes(), headers, HttpStatus.OK);
    }

    @PostMapping("/profile/{userId}/updatePhoto")
    ResponseEntity<Void> updateProfilePhoto(@PathVariable String userId, @RequestParam("profilePhoto") MultipartFile file) {
        UserProfilePhoto userProfilePhoto;
        try {
            userProfilePhoto = new UserProfilePhoto(userId, file.getContentType(), file.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        profileService.updateProfilePhoto(userProfilePhoto);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
