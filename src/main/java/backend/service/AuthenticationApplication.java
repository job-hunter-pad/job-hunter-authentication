package backend.service;

import backend.service.authentication.repository.UserRepository;
import backend.service.profile.repository.ProfilePhotoRepository;
import backend.service.profile.repository.ProfileRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}, scanBasePackages = {"backend.service.authentication", "backend.service.profile"})
@EnableMongoRepositories(basePackageClasses = {UserRepository.class, ProfileRepository.class, ProfilePhotoRepository.class})
public class AuthenticationApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationApplication.class, args);
    }
}
