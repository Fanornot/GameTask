package chocolate.gametask.dto;


import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserProfileDTO {
    private Long id;
    private String username;
    private String email;
    private Integer bonusBalance;
    private String loyaltyStatus;
    private Set<String> roles;
    private LocalDateTime createdAt;
}
