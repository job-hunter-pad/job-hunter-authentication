package backend.service.authentication.model;

import backend.service.authentication.model.validators.ValidEmail;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/*
    Data Class for the Users
 */
@Data
public class User {

    @Id
    private String id;

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

    @NotNull
    @NotEmpty
    private UserType userType;

    private boolean valid;

    public User(@NotNull @NotEmpty String name, @NotNull @NotEmpty String email, @NotNull @NotEmpty String password, @NotNull @NotEmpty UserType userType) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

}
