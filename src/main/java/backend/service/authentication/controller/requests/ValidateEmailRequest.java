package backend.service.authentication.controller.requests;

import lombok.Getter;

@Getter
public class ValidateEmailRequest {

    private String login_token;
}
