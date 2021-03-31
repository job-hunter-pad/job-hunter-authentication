package backend.service.authentication.kafka.model;

public class Email {

    private String email;
    private String subject;
    private String body;

    public Email() {}

    public Email(String email, String subject, String body) {
        this.email = email;
        this.subject = subject;
        this.body = body;
    }

    public String getEmail() { return email; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }

    public void setEmail(String email) { this.email = email; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setBody(String body) { this.body = body; }

    @Override
    public String toString() {
        return "Email{" +
                "email='" + email + '\'' +
                ", subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

}