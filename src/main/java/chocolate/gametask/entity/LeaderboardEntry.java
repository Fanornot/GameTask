package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "leaderboard_entries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LeaderboardEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String username; // Анонимный псевдоним

    @Column(nullable = false)
    private Integer totalBonusEarned;

    @Column(nullable = false, length = 20)
    private String league; // BRONZE, SILVER, GOLD, PLATINUM, DIAMOND

    @Column(nullable = false, length = 10)
    private String monthYear; // "2026-06"
}