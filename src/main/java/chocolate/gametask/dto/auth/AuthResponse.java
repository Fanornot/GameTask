package chocolate.gametask.dto.auth;



import lombok.*;

import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private Set<String> roles;
}
