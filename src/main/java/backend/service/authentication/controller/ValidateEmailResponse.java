package backend.service.authentication.controller;

import lombok.Setter;

@Setter
public class ValidateEmailResponse {
    private String message;

    public ValidateEmailResponse(String message) {
        this.message = message;
    }
}
