package chocolate.gametask.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_quests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "quest_id", "assigned_date"})
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserQuest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quest_id", nullable = false)
    private Quest quest;

    @Builder.Default
    @Column(nullable = false)
    private Integer progress = 0;

    @Builder.Default
    @Column(nullable = false)
    private Boolean completed = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean claimed = false;

    @Column(nullable = false)
    private LocalDate assignedDate;

    @Column
    private LocalDateTime completedAt;
}