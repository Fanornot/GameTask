package chocolate.gametask.dto;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateQuestRequest {
    @NotBlank @Size(max = 200)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotBlank
    private String type; // DAILY, SEASONAL

    @Min(1) @Max(1000)
    private Integer targetCount;

    @Min(1) @Max(10000)
    private Integer rewardAmount;

    private String targetAudience; // ALL, NEW, VIP, SLEEPING
}
