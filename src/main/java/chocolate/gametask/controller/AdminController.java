package chocolate.gametask.controller;

import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.service.LeaderboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Административные операции")
public class AdminController {

    private final LeaderboardService leaderboardService;

    @PostMapping("/leaderboard/calculate")
    public ResponseEntity<ApiResponseDTO<String>> recalculateLeaderboard() {
        leaderboardService.calculateMonthlyLeaderboard();
        return ResponseEntity.ok(ApiResponseDTO.ok("Лидерборд пересчитан"));
    }
}
