package portfolio_service.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    // Counter — tracks how many trades were executed
    @Bean
    public Counter tradeExecutionCounter(MeterRegistry registry) {
        return Counter.builder("fidelity.trades.executed")
                .description("Total number of trades executed")
                .tag("service", "trade-service")
                .register(registry);
    }

    // Counter — tracks portfolio rebalancing operations
    @Bean
    public Counter portfolioRebalanceCounter(MeterRegistry registry) {
        return Counter.builder("fidelity.portfolio.rebalanced")
                .description("Total number of portfolio rebalancing operations")
                .tag("service", "portfolio-service")
                .register(registry);
    }

    // Timer — measures how long trade execution takes
    @Bean
    public Timer tradeExecutionTimer(MeterRegistry registry) {
        return Timer.builder("fidelity.trades.execution.time")
                .description("Time taken to execute a trade")
                .tag("service", "trade-service")
                .register(registry);
    }

    // Timer — measures portfolio fetch time
    @Bean
    public Timer portfolioFetchTimer(MeterRegistry registry) {
        return Timer.builder("fidelity.portfolio.fetch.time")
                .description("Time taken to fetch portfolio data")
                .tag("service", "portfolio-service")
                .register(registry);
    }
}