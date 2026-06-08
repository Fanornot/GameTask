package chocolate.gametask.controller;

import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.CreateQuestRequest;
import chocolate.gametask.dto.QuestDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.QuestService;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/quests")
@RequiredArgsConstructor
@Tag(name = "Quests", description = "Квесты")
public class QuestController {

    private final QuestService questService;
    private final UserService userService;

    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<List<QuestDTO>>> active(
            @RequestParam(required = false) String type) {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(questService.getActiveQuests(user, type)));
    }

    @PostMapping("/{id}/claim")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> claim(@PathVariable Long id) {
        User user = userService.getCurrentUserEntity();
        Integer reward = questService.claimReward(user, id);
        return ResponseEntity.ok(ApiResponseDTO.ok(Map.of("reward", reward), "Награда получена"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MARKETING', 'ADMIN')")
    public ResponseEntity<ApiResponseDTO<QuestDTO>> create(@Valid @RequestBody CreateQuestRequest request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(questService.createQuest(request)));
    }
}