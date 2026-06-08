package chocolate.gametask.service;


import chocolate.gametask.dto.auth.AuthResponse;
import chocolate.gametask.dto.auth.LoginRequest;
import chocolate.gametask.dto.auth.RegisterRequest;
import chocolate.gametask.entity.Role;
import chocolate.gametask.entity.User;
import chocolate.gametask.exception.BusinessLogicException;
import chocolate.gametask.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.RoleRepository;
import chocolate.gametask.repository.UserRepository;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (Boolean.FALSE.equals(request.getConsentGiven())) {
            throw new BusinessLogicException("Необходимо согласие на обработку персональных данных (152-ФЗ)");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessLogicException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessLogicException("Пользователь с таким email уже существует");
        }

        Role clientRole = roleRepository.findByName("ROLE_CLIENT")
                .orElseThrow(() -> new BusinessLogicException("Роль CLIENT не найдена в БД"));

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .bonusBalance(0)
                .loyaltyStatus("NEW")
                .consentGiven(true)
                .roles(Set.of(clientRole))
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessLogicException("Пользователь не найден"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = tokenProvider.generateToken(user.getUsername());
        Set<String> roles = user.getRoles().stream()
                .map(r -> r.getName().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .roles(roles)
                .build();
    }
}
