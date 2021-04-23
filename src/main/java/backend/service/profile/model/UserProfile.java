package backend.service.profile.model;

import backend.service.authentication.model.UserType;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class UserProfile {

    @Id
    private String userId;
    private String name;
    private String email;
    private UserType userType;
    private String location;
    private String description;
    private String phoneNumber;
    private List<Review> reviews;
    private List<String> skills;

    public UserProfile(String userId, String email, String name, UserType userType) {
        this.email = email;
        this.userId = userId;
        this.name = name;
        this.userType = userType;
        this.reviews = new ArrayList<>();
        this.skills = new ArrayList<>();
    }

    public void addReview(Review review) {
        this.reviews.add(review);
    }
}
