package backend.service.authentication.controller;

import backend.service.authentication.controller.requests.RegisterRequest;
import backend.service.authentication.controller.responses.LoginResponse;
import backend.service.authentication.controller.responses.RegisterResponse;
import backend.service.authentication.controller.responses.ValidateEmailResponse;
import backend.service.authentication.kafka.model.Email;
import backend.service.authentication.kafka.producer.Producer;
import backend.service.authentication.model.User;
import backend.service.authentication.model.UserType;
import backend.service.authentication.repository.UserRepository;
import backend.service.authentication.repository.token.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private Producer kafkaProducer;

    @PostMapping("/login")
    LoginResponse login(@RequestBody User user) {
        LoginResponse loginResponse = new LoginResponse();
        if (checkAccountExists(user.getEmail()) != null) {
            if (checkCredentials(user)) {
                User dbUser = userRepository.findByEmail(user.getEmail());
                final String token = jwtTokenUtil.generateToken(dbUser);
                if (checkIfAccountValidated(token)) {
                    loginResponse.setSuccess(true);
                    loginResponse.setLogin_token(token);
                } else {
                    loginResponse.setSuccess(false);
                    loginResponse.setFail_message("Account not validated");
                }
            } else {
                loginResponse.setSuccess(false);
                loginResponse.setFail_message("Invalid credentials");
            }
        } else {
            loginResponse.setSuccess(false);
            loginResponse.setFail_message("Account does not exist");
        }

        return loginResponse;
    }

    @PostMapping("/register")
    RegisterResponse register(@RequestBody RegisterRequest registerRequest) {
        User user = new User(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword(),
                UserType.valueOf(registerRequest.getUserType().toUpperCase()));
        RegisterResponse registerResponse = new RegisterResponse();
        if (checkAccountExists(user.getEmail()) != null) {
            registerResponse.setSuccess(false);
            registerResponse.setFail_message("Account already exists");
        } else {
            encryptPassword(user);
            userRepository.save(user);
            final String token = jwtTokenUtil.generateToken(user);
            registerResponse.setSuccess(true);
            registerResponse.setLogin_token(token);

            // send validation email
            Email email = new Email();
            final String account_key = jwtTokenUtil.generateToken(email);

            email.setEmail(user.getEmail());
            email.setSubject("Account Validation Job Hunter");
            email.setBody(
                    "<h1>Welcome " + user.getName() + "</h1>" +
                            "<br><br>" +
                            "<h4>You are almost ready to start enjoying Job Hunter</h4>" +
                            "<br>" +
                            "<a href=\"localhost:8080/validateEmail/" + account_key + "\">Click here to confirm your account</a>"
            );

            kafkaProducer.postEmail(email);

        }
        return registerResponse;
    }

    @GetMapping("/validateEmail/{account_key}")
    ValidateEmailResponse validateAccount(@PathVariable String account_key) {
        if (checkIfAccountValidated(account_key)) {
            return new ValidateEmailResponse("Account already validated");
        } else {
            validateEmail(account_key);
            return new ValidateEmailResponse("Account validated");
        }
    }


    private User checkAccountExists(String email) {
        return userRepository.findByEmail(email);
    }

    private boolean checkIfAccountValidated(String account_key) {
        String id = jwtTokenUtil.getIdFromToken(account_key);
        User user = userRepository.findByEmail(id);
        return user != null;
    }

    private void validateEmail(String account_key) {
        String email = jwtTokenUtil.getIdFromToken(account_key);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setValid(true);
            userRepository.save(user);
        }
    }

    private boolean checkCredentials(User user) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        User dbUser = userRepository.findByEmail(user.getEmail());
        return dbUser != null && bCryptPasswordEncoder.matches(user.getPassword(), dbUser.getPassword());
    }

    private void encryptPassword(User user) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encryptedPass = bCryptPasswordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPass);
    }

}
