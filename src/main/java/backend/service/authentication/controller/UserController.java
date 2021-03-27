package backend.service.authentication.controller;

import backend.service.authentication.model.User;
import backend.service.authentication.model.UserType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @PostMapping("/api/account")
    public User GETUsers(@RequestParam(value = "name") String name,
                         @RequestParam(value = "email") String email,
                         @RequestParam(value = "password") String password,
                         @RequestParam(value = "userType") UserType userType,
                         @RequestParam(value = "id") String id){

        return new User(id,name,email,new BCryptPasswordEncoder().encode(password),userType);
    }


}
