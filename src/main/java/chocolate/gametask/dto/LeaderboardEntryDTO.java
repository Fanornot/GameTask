package chocolate.gametask.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LeaderboardEntryDTO {
    private Integer rank;
    private String username;
    private Integer totalBonusEarned;
    private String league;
}