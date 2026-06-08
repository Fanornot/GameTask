package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_prizes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserPrize {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prize_id", nullable = false)
    private Prize prize;

    @Column(length = 50)
    private String promoCode;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @Builder.Default
    @Column(length = 20)
    private String status = "PENDING"; // PENDING, ISSUED, REDEEMED
}