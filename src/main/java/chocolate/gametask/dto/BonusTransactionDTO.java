package chocolate.gametask.dto;
import lombok.*;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class BonusTransactionDTO {
    private Long id;
    private Integer amount;
    private String transactionType;
    private String sourceType;
    private String description;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
