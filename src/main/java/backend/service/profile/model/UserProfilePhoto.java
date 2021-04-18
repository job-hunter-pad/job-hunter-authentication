package backend.service.profile.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfilePhoto {

    @Id
    private String userId;
    private String contentType;
    private byte[] profilePhotoBytes;
}
