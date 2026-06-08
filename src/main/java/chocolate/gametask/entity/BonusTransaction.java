package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bonus_transactions", indexes = {
        @Index(name = "idx_user_expires", columnList = "user_id, expires_at")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BonusTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer amount; // Может уменьшаться при частичном списании (FIFO)

    @Column(nullable = false, length = 10)
    private String transactionType; // CREDIT, DEBIT

    @Column(nullable = false, length = 50)
    private String sourceType; // LOAN_PAYMENT, QUEST, WHEEL, DAILY, PURCHASE, FREEZE

    @Column(length = 255)
    private String description;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt; // null для DEBIT

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}