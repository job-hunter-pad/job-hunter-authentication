package backend.service.authentication.model;

import backend.service.authentication.model.services.PasswordMatches;
import backend.service.authentication.model.services.ValidEmail;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/*
    Data Class for the Users
 */
@Data
@PasswordMatches
public class User {

    @NotNull
    @NotEmpty
    private String name;

    @ValidEmail
    @NotNull
    @NotEmpty
    private String email;

    @NotNull
    @NotEmpty
    private String password;
    private String matchingPassword;

    @NotNull
    @NotEmpty
    private UserType userType;

    public User(@NotNull @NotEmpty String name, @NotNull @NotEmpty String email, @NotNull @NotEmpty String password, String matchingPassword, @NotNull @NotEmpty UserType userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.matchingPassword = matchingPassword;
        this.userType = userType;
    }

}
