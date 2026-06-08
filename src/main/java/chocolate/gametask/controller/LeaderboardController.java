package chocolate.gametask.controller;
import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.LeaderboardEntryDTO;
import chocolate.gametask.service.LeaderboardService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Анонимный лидерборд по лигам")
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping("/monthly")
    public ResponseEntity<ApiResponseDTO<List<LeaderboardEntryDTO>>> monthly(
            @RequestParam(required = false) String monthYear) {
        return ResponseEntity.ok(ApiResponseDTO.ok(leaderboardService.getMonthly(monthYear)));
    }
}
