package chocolate.gametask.controller;


import chocolate.gametask.dto.ApiResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analyst")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MARKETING_ANALYST', 'MARKETING', 'ADMIN')")
@Tag(name = "Analyst", description = "Дашборд маркетинговой аналитики")
public class AnalystController {

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> dashboard() {
        // Здесь должна быть реальная аналитика из БД
        Map<String, Object> demo = Map.of(
                "dau", 1245,
                "mau", 8730,
                "dau_mau_ratio", "14.3%",
                "quests_completed_today", 342,
                "wheel_spins_today", 89,
                "bonuses_issued_today", 12450,
                "conversion_rate", "27%"
        );
        return ResponseEntity.ok(ApiResponseDTO.ok(demo));
    }
}
