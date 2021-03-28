package backend.service.authentication.controller;

import lombok.Getter;

@Getter
public class ValidateEmailRequest {

    private String login_token;
}
