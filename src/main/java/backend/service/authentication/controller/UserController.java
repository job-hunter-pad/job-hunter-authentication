package backend.service.authentication.controller;

import backend.service.authentication.controller.requests.RegisterRequest;
import backend.service.authentication.controller.requests.ResetPasswordRequest;
import backend.service.authentication.controller.requests.ValidateIdRequest;
import backend.service.authentication.controller.responses.*;
import backend.service.authentication.kafka.model.Email;
import backend.service.authentication.kafka.producer.Producer;
import backend.service.authentication.model.User;
import backend.service.authentication.model.UserType;
import backend.service.authentication.repository.UserRepository;
import backend.service.authentication.repository.token.JwtTokenUtil;
import backend.service.profile.kafka.producer.UserProfileProducer;
import backend.service.profile.model.UserProfile;
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
    private final UserProfileProducer userProfileProducer;

    public UserController(UserRepository userRepository,
                          JwtTokenUtil jwtTokenUtil,
                          Producer kafkaProducer,
                          ProfileService profileService,
                          UserProfileProducer userProfileProducer) {
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
        this.kafkaProducer = kafkaProducer;
        this.profileService = profileService;
        this.userProfileProducer = userProfileProducer;
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

            UserProfile userProfile = profileService.createProfile(user);
            userProfileProducer.postUserProfile(userProfile);

            // send validation email
            Email email = new Email();
            email.setEmail(user.getEmail());
            email.setSubject("Account Validation Job Hunter");

            String baseUrl = System.getenv("BASE_URL");

            final String account_key = jwtTokenUtil.generateToken(email);
            final String validationUrl = baseUrl + "/validateEmail/" + account_key;

            email.setBody(
                    "<h1>Welcome, " + user.getName() + "!</h1>" +
                            "<h4>You’re just one click away from getting started with Job Hunter. All you need to do is verify your email address to activate your account:</h4>" +
                            "<br>" +
                            "<a href=\"" + validationUrl + "\">Click here to confirm your account</a>" +
                            "<br><br>" +
                            "<h4>Thanks, </h4>" +
                            "<h4>Job Hunter Team</h4>"
            );

            kafkaProducer.postEmail(email);

        }
        return registerResponse;
    }

    @PostMapping("/sendResetPasswordEmail")
    SendResetPasswordEmailResponse sendResetPasswordEmail(@RequestBody String userEmail) {
        SendResetPasswordEmailResponse sendResetPasswordEmailResponse = new SendResetPasswordEmailResponse();

        if (checkAccountExists(userEmail) == null) {
            sendResetPasswordEmailResponse.setMessage("Account does not exist");
        } else {
            User user = userRepository.findByEmail(userEmail);

            // send validation email
            Email email = new Email();
            email.setEmail(userEmail);
            email.setSubject("Reset Password Job Hunter");

            String baseUrl = System.getenv("BASE_URL");

            final String account_key = jwtTokenUtil.generateToken(email);
            final String validationUrl = baseUrl + "/resetPassword/" + account_key;

            email.setBody(
                    "<h1>Hello, " + user.getName() + "!</h1>" +
                            "<h4>You have requested a password reset. If this was not requested by you please ignore this email, otherwise click the link below </h4>" +
                            "<br>" +
                            "<a href=\"" + validationUrl + "\">Click here to reset your password</a>" +
                            "<br><br>" +
                            "<h4>Thanks, </h4>" +
                            "<h4>Job Hunter Team</h4>"
            );

            kafkaProducer.postEmail(email);

            sendResetPasswordEmailResponse.setMessage("Email sent successfully");
        }

        return sendResetPasswordEmailResponse;
    }

    @PostMapping("/resetPassword")
    ResetPasswordResponse resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        ResetPasswordResponse resetPasswordResponse = new ResetPasswordResponse();

        String password = resetPasswordRequest.getPassword();
        String confirmPassword = resetPasswordRequest.getConfirmPassword();
        String userEmail = resetPasswordRequest.getEmail();

        if (!password.equals(confirmPassword)) {
            resetPasswordResponse.setMessage("Passwords do not match");
        } else if (checkAccountExists(userEmail) == null) {
            resetPasswordResponse.setMessage("Account does not exist");
        } else {
            User user = userRepository.findByEmail(userEmail);

            user.setPassword(password);
            encryptPassword(user);

            userRepository.save(user);

            resetPasswordResponse.setMessage("Password changed successfully");
        }

        return resetPasswordResponse;
    }


    @PostMapping("/validateId")
    ValidateIdResponse validateToken(@RequestBody ValidateIdRequest validateIdRequest) {
        Boolean valid = jwtTokenUtil.validateTokenUserId(validateIdRequest.getToken(), validateIdRequest.getId());
        return new ValidateIdResponse(valid);
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
