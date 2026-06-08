package chocolate.gametask.dto;


import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PrizeDTO {
    private Long id;
    private String name;
    private String description;
    private String category;
    private Integer cost;
    private Integer stock;
    private String imageUrl;
    private Boolean canAfford; // вычисляется на основе баланса пользователя
}