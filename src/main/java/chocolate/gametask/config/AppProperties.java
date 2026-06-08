package chocolate.gametask.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
public class AppProperties {
    private Wheel wheel = new Wheel();
    private Daily daily = new Daily();
    private Bonus bonus = new Bonus();

    @Data
    public static class Wheel {
        private int freeSpinPerWeek = 1;
        private int paidSpinPerDay = 5;
        private int spinCost = 50;
    }

    @Data
    public static class Daily {
        private int freezeCost = 100;
        private int baseReward = 5;
    }

    @Data
    public static class Bonus {
        private int expiryMonths = 12;
    }
}