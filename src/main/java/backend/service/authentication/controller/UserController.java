package backend.service.authentication.controller;

import backend.service.authentication.model.User;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @PostMapping("/login")
    Object login(@RequestBody User user, @RequestParam int loginCase) {
        //TODO: generate token;
        if (loginCase == 1) {//TODO: exista user in Baza de date;
            return new LoginResponse(true, "token");
        }
        if (loginCase == 2)//TODO: credentiale invalide
        {
            return new LoginResponse(false, null, "Invalid credentials");
        }
        if (loginCase == 3)//TODO: contul nu exista
        {
            return new LoginResponse(false, null, "Account does not exist");
        }
        if (loginCase == 4)//TODO: contul nu a fost validat
        {
            return new LoginResponse(false, null, "Account not validated");
        }
        return ResponseEntity.badRequest();
    }

    @PostMapping("/register")
    Object register(@RequestBody RegisterRequest registerRequest, @RequestParam int registerCase) {
        if (registerCase == 1) {
            return new RegisterResponse(true, "token");
        }
        if (registerCase == 2) {
            return new RegisterResponse(false, null, "Account already exists");
        }
        return ResponseEntity.badRequest();
    }

    //not working
    @GetMapping("/validateEmail")
    Object validateEmail(@RequestBody ValidateEmailRequest validateEmailRequest, @RequestParam int emailCase) {
        if (emailCase == 1) {
            return new ValidateEmailResponse("Account validated");
        }
        if (emailCase == 2) {
            return new ValidateEmailResponse("Account already validated");
        }
        return ResponseEntity.badRequest();
    }
}
