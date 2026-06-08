package chocolate.gametask.controller;


import chocolate.gametask.dto.ApiResponseDTO;
import chocolate.gametask.dto.auth.AuthResponse;
import chocolate.gametask.dto.auth.LoginRequest;
import chocolate.gametask.dto.auth.RegisterRequest;
import chocolate.gametask.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Регистрация и вход")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Регистрация нового клиента (с согласием 152-ФЗ)")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(authService.register(request), "Регистрация успешна"));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход, получение JWT-токена")
    public ResponseEntity<ApiResponseDTO<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponseDTO.ok(authService.login(request)));
    }
}
