package chocolate.gametask.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quests")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Quest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 20)
    private String type; // DAILY, SEASONAL, CUSTOM

    @Column(nullable = false)
    private Integer targetCount;

    @Column(nullable = false)
    private Integer rewardAmount;

    @Builder.Default
    private Boolean active = true;

    @Column(length = 50)
    private String targetAudience; // ALL, NEW, VIP, SLEEPING
}