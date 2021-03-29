package backend.service.authentication.controller.responses;

public class RegisterResponse {

    public boolean success;
    public String login_token;
    public String fail_message;

    public RegisterResponse(boolean success, String login_token) {
        this.success = success;
        this.login_token = login_token;
    }

    public RegisterResponse(boolean success, String login_token, String fail_message) {
        this.success = success;
        this.login_token = login_token;
        this.fail_message = fail_message;
    }
}