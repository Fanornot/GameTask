package chocolate.gametask.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prizes")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Prize {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, length = 20)
    private String category; // FINANCIAL, PARTNER, MERCH, CHARITY, EXCLUSIVE

    @Column(nullable = false)
    private Integer cost;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;
}