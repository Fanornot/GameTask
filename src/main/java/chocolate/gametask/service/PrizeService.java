package chocolate.gametask.service;


import chocolate.gametask.dto.PrizeDTO;
import chocolate.gametask.entity.Prize;
import chocolate.gametask.entity.User;
import chocolate.gametask.entity.UserPrize;
import chocolate.gametask.exception.BusinessLogicException;
import chocolate.gametask.exception.InsufficientBalanceException;
import chocolate.gametask.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.PrizeRepository;
import chocolate.gametask.repository.UserPrizeRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrizeService {

    private final PrizeRepository prizeRepository;
    private final UserPrizeRepository userPrizeRepository;
    private final BonusService bonusService;

    @Transactional(readOnly = true)
    public List<PrizeDTO> getAll(User user) {
        return prizeRepository.findByActiveTrue().stream()
                .map(p -> PrizeDTO.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .category(p.getCategory())
                        .cost(p.getCost())
                        .stock(p.getStock())
                        .imageUrl(p.getImageUrl())
                        .canAfford(user.getBonusBalance() >= p.getCost())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public String purchase(User user, Long prizeId) {
        Prize prize = prizeRepository.findById(prizeId)
                .orElseThrow(() -> new ResourceNotFoundException("Приз не найден"));

        if (!Boolean.TRUE.equals(prize.getActive())) {
            throw new BusinessLogicException("Приз недоступен");
        }
        if (prize.getStock() <= 0) {
            throw new BusinessLogicException("Приз закончился");
        }
        if (user.getBonusBalance() < prize.getCost()) {
            throw new InsufficientBalanceException(String.format(
                    "До приза осталось %d бонусов. Получите их за выполнение квестов!",
                    prize.getCost() - user.getBonusBalance()));
        }

        bonusService.debitBonus(user, prize.getCost(), "PURCHASE", "Покупка: " + prize.getName());

        prize.setStock(prize.getStock() - 1);
        prizeRepository.save(prize);

        String promoCode = generatePromoCode(prize.getCategory());

        UserPrize userPrize = UserPrize.builder()
                .user(user)
                .prize(prize)
                .promoCode(promoCode)
                .purchasedAt(LocalDateTime.now())
                .status(prize.getCategory().equals("PARTNER") || prize.getCategory().equals("FINANCIAL") ? "ISSUED" : "PENDING")
                .build();
        userPrizeRepository.save(userPrize);

        return promoCode;
    }

    private String generatePromoCode(String category) {
        String prefix = switch (category) {
            case "PARTNER" -> "SKS-PRT-";
            case "FINANCIAL" -> "SKS-FIN-";
            case "MERCH" -> "SKS-MRC-";
            default -> "SKS-";
        };
        return prefix + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
