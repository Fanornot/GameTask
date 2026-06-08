package chocolate.gametask.controller;


import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.entity.DailyStreak;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.DailyStreakService;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
@Tag(name = "Daily", description = "Ежедневный вход и заморозка стрика")
public class DailyController {

    private final DailyStreakService dailyStreakService;
    private final UserService userService;

    @GetMapping("/streak")
    public ResponseEntity<ApiResponseDTO<DailyStreak>> getStreak() {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(dailyStreakService.getStreak(user)));
    }

    @PostMapping("/checkin")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> checkin() {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(dailyStreakService.claimDailyReward(user)));
    }

    @PostMapping("/freeze")
    public ResponseEntity<ApiResponseDTO<String>> buyFreeze() {
        User user = userService.getCurrentUserEntity();
        dailyStreakService.buyFreeze(user);
        return ResponseEntity.ok(ApiResponseDTO.ok("Заморозка куплена"));
    }
}