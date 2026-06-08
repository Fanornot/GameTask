package chocolate.gametask.service;


import chocolate.gametask.config.AppProperties;
import chocolate.gametask.dto.BonusTransactionDTO;
import chocolate.gametask.entity.BonusTransaction;
import chocolate.gametask.entity.User;
import chocolate.gametask.exception.InsufficientBalanceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import chocolate.gametask.repository.BonusTransactionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusService {

    private final BonusTransactionRepository transactionRepository;
    private final AppProperties appProperties;

    @Transactional
    public void creditBonus(User user, Integer amount, String sourceType, String description) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Сумма бонуса должна быть положительной");
        }

        BonusTransaction tx = BonusTransaction.builder()
                .user(user)
                .amount(amount)
                .transactionType("CREDIT")
                .sourceType(sourceType)
                .description(description)
                .expiresAt(LocalDateTime.now().plusMonths(appProperties.getBonus().getExpiryMonths()))
                .build();
        transactionRepository.save(tx);

        user.setBonusBalance(user.getBonusBalance() + amount);
        log.info("Начислено {} бонусов пользователю {} ({})", amount, user.getUsername(), sourceType);
    }

    @Transactional
    public void debitBonus(User user, Integer amount, String sourceType, String description) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Сумма списания должна быть положительной");
        }
        if (user.getBonusBalance() < amount) {
            throw new InsufficientBalanceException(
                    String.format("Недостаточно бонусов: требуется %d, доступно %d. До цели осталось %d бонусов",
                            amount, user.getBonusBalance(), amount - user.getBonusBalance()));
        }

        // FIFO списание
        LocalDateTime now = LocalDateTime.now();
        List<BonusTransaction> activeCredits = transactionRepository.findActiveCreditsFifo(user, now);

        int remaining = amount;
        for (BonusTransaction credit : activeCredits) {
            if (remaining <= 0) break;

            int toDebit = Math.min(remaining, credit.getAmount());
            credit.setAmount(credit.getAmount() - toDebit);
            remaining -= toDebit;

            if (credit.getAmount() == 0) {
                transactionRepository.delete(credit);
            } else {
                transactionRepository.save(credit);
            }
        }

        if (remaining > 0) {
            throw new InsufficientBalanceException("Ошибка FIFO-списания: недостаточно активных транзакций");
        }

        // Запись DEBIT-транзакции для аудита
        BonusTransaction debitTx = BonusTransaction.builder()
                .user(user)
                .amount(amount)
                .transactionType("DEBIT")
                .sourceType(sourceType)
                .description(description)
                .build();
        transactionRepository.save(debitTx);

        user.setBonusBalance(user.getBonusBalance() - amount);
        log.info("Списано {} бонусов у пользователя {} ({})", amount, user.getUsername(), sourceType);
    }

    @Transactional(readOnly = true)
    public List<BonusTransactionDTO> getTransactionHistory(User user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(tx -> BonusTransactionDTO.builder()
                        .id(tx.getId())
                        .amount(tx.getAmount())
                        .transactionType(tx.getTransactionType())
                        .sourceType(tx.getSourceType())
                        .description(tx.getDescription())
                        .expiresAt(tx.getExpiresAt())
                        .createdAt(tx.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Integer getActiveBalance(User user) {
        return transactionRepository.calculateActiveBalance(user, LocalDateTime.now());
    }
}
