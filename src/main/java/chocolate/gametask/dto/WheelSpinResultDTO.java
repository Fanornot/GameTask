package chocolate.gametask.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class WheelSpinResultDTO {
    private Integer rewardAmount;
    private String rewardType;
    private String rewardDescription;
}