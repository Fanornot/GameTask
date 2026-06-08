package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "wheel_spins", indexes = {
        @Index(name = "idx_user_date", columnList = "user_id, spun_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WheelSpin {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Boolean isFree;

    @Column(nullable = false)
    private Integer rewardAmount;

    @Column(nullable = false, length = 30)
    private String rewardType; // BONUS, DISCOUNT, BADGE, PRIZE

    @Column(length = 200)
    private String rewardDescription;

    @Column(nullable = false)
    private LocalDateTime spunAt;

    @PrePersist
    public void prePersist() {
        if (spunAt == null) spunAt = LocalDateTime.now();
    }
}