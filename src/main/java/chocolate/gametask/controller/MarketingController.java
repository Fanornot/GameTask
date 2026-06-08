package chocolate.gametask.controller;


import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.CreateQuestRequest;
import chocolate.gametask.dto.QuestDTO;
import chocolate.gametask.service.QuestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/marketing")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MARKETING', 'ADMIN')")
@Tag(name = "Marketing", description = "Операции маркетинга")
public class MarketingController {
    private final QuestService questService;
    @PostMapping("/quests")
    public ResponseEntity<ApiResponseDTO<QuestDTO>> createQuest(@Valid @RequestBody CreateQuestRequest request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(questService.createQuest(request), "Квест создан"));
    }
}
