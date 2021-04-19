package backend.service.authentication.controller.responses;

import backend.service.authentication.model.UserType;
import lombok.Value;

@Value
public class UserData {
    String userId;
    UserType userType;
}
