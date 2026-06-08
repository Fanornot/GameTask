package chocolate.gametask.service;

import chocolate.gametask.dto.UserProfileDTO;
import chocolate.gametask.entity.User;
import chocolate.gametask.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.UserRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User getCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден: " + username));
    }

    @Transactional(readOnly = true)
    public UserProfileDTO getCurrentUserProfile() {
        User user = getCurrentUserEntity();
        return UserProfileDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .bonusBalance(user.getBonusBalance())
                .loyaltyStatus(user.getLoyaltyStatus())
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().replace("ROLE_", ""))
                        .collect(Collectors.toSet()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}