package backend.service.authentication.controller.responses;

import lombok.Setter;

@Setter
public class LoginResponse {

    public boolean success;
    public String login_token;
    public String fail_message;

}
