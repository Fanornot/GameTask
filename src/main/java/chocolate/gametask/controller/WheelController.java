package chocolate.gametask.controller;

import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.WheelSpinResultDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.service.UserService;
import chocolate.gametask.service.WheelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/wheel")
@RequiredArgsConstructor
@Tag(name = "Wheel", description = "Колесо фортуны")
public class WheelController {

    private final WheelService wheelService;
    private final UserService userService;

    @PostMapping("/spin")
    @Operation(summary = "Прокрутка колеса: free=true — бесплатно (1/нед), free=false — за 50 бонусов (до 5/день)")
    public ResponseEntity<ApiResponseDTO<WheelSpinResultDTO>> spin(
            @RequestParam(defaultValue = "false") boolean free) {
        User user = userService.getCurrentUserEntity();
        return ResponseEntity.ok(ApiResponseDTO.ok(wheelService.spin(user, free)));
    }

    @GetMapping("/probabilities")
    @Operation(summary = "Публичные вероятности призов (compliance)")
    public ResponseEntity<ApiResponseDTO<Map<String, Object>>> probabilities() {
        return ResponseEntity.ok(ApiResponseDTO.ok(wheelService.getProbabilities()));
    }
}
