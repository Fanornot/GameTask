package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "achievements")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Achievement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 10)
    private String icon; // эмодзи

    @Column(nullable = false, length = 50)
    private String conditionType; // LOAN_COUNT, BONUS_BALANCE, MEMBERSHIP_DAYS, PERFECT_PAYMENT, EARLY_VISIT

    @Column(nullable = false)
    private Integer threshold;

    @Column(nullable = false)
    private Integer rewardAmount;
}
