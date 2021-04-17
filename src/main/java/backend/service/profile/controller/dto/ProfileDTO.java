package backend.service.profile.controller.dto;

import lombok.Value;

@Value
public class ProfileDTO {
    String name;
    String description;
    String location;
    String phoneNumber;
}
