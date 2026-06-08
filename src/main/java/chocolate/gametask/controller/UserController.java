package chocolate.gametask.controller;

import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.UserProfileDTO;
import chocolate.gametask.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Профиль пользователя")
public class UserController {

    private final UserService userService;
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserProfileDTO>> me() {
        return ResponseEntity.ok(ApiResponseDTO.ok(userService.getCurrentUserProfile()));
    }
}
