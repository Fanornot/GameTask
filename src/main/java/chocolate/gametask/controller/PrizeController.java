package chocolate.gametask.controller;

import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.PrizeDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.PrizeService;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/prizes")
@RequiredArgsConstructor
@Tag(name = "Prizes", description = "Каталог призов")
public class PrizeController {

    private final PrizeService prizeService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PrizeDTO>>> list() {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(prizeService.getAll(user)));
    }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> purchase(@PathVariable Long id) {
        User user = userService.getCurrentUserEntity();
        String promoCode = prizeService.purchase(user, id);
        return ResponseEntity.ok(ApiResponseDTO.ok(Map.of("promoCode", promoCode), "Приз куплен!"));
    }
}