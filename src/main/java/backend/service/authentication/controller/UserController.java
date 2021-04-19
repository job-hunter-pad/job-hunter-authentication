package backend.service.authentication.controller;

import backend.service.authentication.controller.requests.RegisterRequest;
import backend.service.authentication.controller.responses.LoginResponse;
import backend.service.authentication.controller.responses.RegisterResponse;
import backend.service.authentication.controller.responses.UserData;
import backend.service.authentication.controller.responses.ValidateEmailResponse;
import backend.service.authentication.kafka.model.Email;
import backend.service.authentication.kafka.producer.Producer;
import backend.service.authentication.model.User;
import backend.service.authentication.model.UserType;
import backend.service.authentication.repository.UserRepository;
import backend.service.authentication.repository.token.JwtTokenUtil;
import backend.service.profile.service.ProfileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

    private final UserRepository userRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final Producer kafkaProducer;

    private final ProfileService profileService;

    public UserController(UserRepository userRepository, JwtTokenUtil jwtTokenUtil, Producer kafkaProducer, ProfileService profileService) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.kafkaProducer = kafkaProducer;
        this.profileService = profileService;
    }

    @PostMapping("/login")
    LoginResponse login(@RequestBody User user) {
        LoginResponse loginResponse = new LoginResponse();
        if (checkAccountExists(user.getEmail()) != null) {
            if (checkCredentials(user)) {
                User dbUser = userRepository.findByEmail(user.getEmail());

                if (dbUser.isValid()) {
                    loginResponse.setSuccess(true);
                    final String token = jwtTokenUtil.generateToken(dbUser);
                    loginResponse.setUserData(new UserData(dbUser.getId(), dbUser.getUserType()));
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

            profileService.createProfile(user);

            // send validation email
            Email email = new Email();
            email.setEmail(user.getEmail());
            email.setSubject("Account Validation Job Hunter");

            final String account_key = jwtTokenUtil.generateToken(email);
            final String validationUrl = "http://localhost:8090/validateEmail/" + account_key;

            email.setBody(
                    "<h1>Welcome " + user.getName() + "</h1>" +
                            "<br><br>" +
                            "<h4>You are almost ready to start enjoying Job Hunter</h4>" +
                            "<br>" +
//                            "<p>" + validationUrl + "</p><br>" +
                            "<a href=\"" + validationUrl + "\">Click here to confirm your account</a>"
            );

            kafkaProducer.postEmail(email);

        }
        return registerResponse;
    }

    @PostMapping("/validateEmail")
    ValidateEmailResponse validateAccount(@RequestBody String requestBodyJson) {
        String account_key = "";

        try {
            JsonNode root = new ObjectMapper().readTree(requestBodyJson);
            account_key = root.get("account_key").textValue();
            if (!account_key.isEmpty()) {
                if (checkIfAccountValidated(account_key)) {
                    return new ValidateEmailResponse("Account already validated");
                } else {
                    validateEmail(account_key);
                    return new ValidateEmailResponse("Account validated");
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            if (!account_key.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account key");
            }
        }

        return null;
    }

    private User checkAccountExists(String email) {
        return userRepository.findByEmail(email);
    }

    private boolean checkIfAccountValidated(String account_key) {
        String id = jwtTokenUtil.getIdFromToken(account_key);
        User user = userRepository.findByEmail(id);
        return user.isValid();
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
