package chocolate.gametask.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_streaks", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyStreak {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentStreak = 0;

    @Builder.Default
    @Column(nullable = false)
    private Integer maxStreak = 0;

    @Column
    private LocalDate lastClaimDate;

    @Builder.Default
    @Column(nullable = false)
    private Boolean claimedToday = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer freezesAvailable = 0;
}