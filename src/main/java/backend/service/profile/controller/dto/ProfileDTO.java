package backend.service.profile.controller.dto;

import lombok.Value;

import java.util.List;

@Value
public class ProfileDTO {
    String name;
    String description;
    String location;
    String phoneNumber;
    List<String> skills;
}
