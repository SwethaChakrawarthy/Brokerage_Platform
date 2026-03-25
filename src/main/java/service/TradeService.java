package portfolio_service.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio_service.model.*;
import portfolio_service.repository.AccountRepository;
import portfolio_service.repository.PortfolioRepository;
import portfolio_service.repository.TradeRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TradeService {

    private final TradeRepository tradeRepository;
    private final PortfolioRepository portfolioRepository;
    private final AccountRepository accountRepository;
    private final Counter tradeExecutionCounter;
    private final Timer tradeExecutionTimer;

    @Transactional
    public Trade executeTrade(Long accountId, String symbol,
                              TradeType tradeType, BigDecimal quantity,
                              BigDecimal marketPrice) {

        long start = System.currentTimeMillis();
        log.info("Executing {} trade: {} shares of {} for account {}",
                tradeType, quantity, symbol, accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountId));

        Portfolio portfolio = portfolioRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Portfolio not found for account: " + accountId));

        BigDecimal tradeValue = quantity.multiply(marketPrice);
        validateTrade(portfolio, tradeType, tradeValue, quantity);

        if (tradeType == TradeType.BUY) {
            portfolio.setCashBalance(
                    portfolio.getCashBalance().subtract(tradeValue));
        } else {
            portfolio.setCashBalance(
                    portfolio.getCashBalance().add(tradeValue));
        }

        portfolioRepository.save(portfolio);

        Trade trade = Trade.builder()
                .account(account)
                .symbol(symbol)
                .tradeType(tradeType)
                .quantity(quantity)
                .executionPrice(marketPrice)
                .status(TradeStatus.EXECUTED)
                .executedAt(LocalDateTime.now())
                .build();

        Trade saved = tradeRepository.save(trade);

        // Track metrics
        tradeExecutionCounter.increment();
        long end = System.currentTimeMillis();
        log.info("Trade executed in {}ms. ID: {}. Total trades: {}",
                (end - start), saved.getId(), tradeExecutionCounter.count());

        return saved;
    }

    public List<Trade> getTradesByAccountId(Long accountId) {
        return tradeRepository.findByAccountId(accountId);
    }

    public List<Trade> getExecutedTrades(Long accountId) {
        return tradeRepository.findByAccountIdAndStatus(
                accountId, TradeStatus.EXECUTED);
    }

    private void validateTrade(Portfolio portfolio, TradeType tradeType,
                               BigDecimal tradeValue, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Trade quantity must be positive");
        }
        if (tradeType == TradeType.BUY) {
            if (portfolio.getCashBalance().compareTo(tradeValue) < 0) {
                throw new RuntimeException(
                        String.format("Insufficient funds. Required: %.2f, Available: %.2f",
                                tradeValue, portfolio.getCashBalance()));
            }
        }
    }
}