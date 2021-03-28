package backend.service.authentication.controller;

import lombok.Getter;

@Getter
public class RegisterRequest {

    private String name;
    private String password;
    private String email;
    private String userType;

}
