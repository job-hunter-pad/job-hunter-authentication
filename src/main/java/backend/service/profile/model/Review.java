package backend.service.profile.model;

import lombok.Data;

@Data
public class Review {
    private String reviewerName;
    private String description;
    private Integer reviewScore;

    public Review() {

    }

    public Review(String reviewerName, String description, Integer reviewScore) {
        this.reviewerName = reviewerName;
        this.description = description;
        this.reviewScore = reviewScore;
    }
}
