package chocolate.gametask.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class QuestDTO {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer targetCount;
    private Integer rewardAmount;
    private Integer progress;
    private Boolean completed;
    private Boolean claimed;
}