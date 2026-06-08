package chocolate.gametask.controller;

import chocolate.gametask.dto.AchievementDTO;
import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.AchievementService;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Tag(name = "Achievements", description = "Достижения и бейджи")
public class AchievementController {

    private final AchievementService achievementService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AchievementDTO>>> list() {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(achievementService.getAllWithStatus(user)));
    }
}
