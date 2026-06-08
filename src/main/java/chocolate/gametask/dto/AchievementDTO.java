package chocolate.gametask.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AchievementDTO {
    private Long id;
    private String name;
    private String description;
    private String icon;
    private String conditionType;
    private Integer threshold;
    private Integer rewardAmount;
    private Boolean unlocked;
}
