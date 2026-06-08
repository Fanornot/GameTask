package chocolate.gametask.controller;


import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.BonusTransactionDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.BonusService;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bonus")
@RequiredArgsConstructor
@Tag(name = "Bonus", description = "История бонусных транзакций")
public class BonusController {

    private final BonusService bonusService;
    private final UserService userService;

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponseDTO<List<BonusTransactionDTO>>> history() {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(bonusService.getTransactionHistory(user)));
    }
}