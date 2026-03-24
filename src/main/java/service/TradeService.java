package portfolio_service.service;

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

    @Transactional
    public Trade executeTrade(Long accountId, String symbol,
                              TradeType tradeType, BigDecimal quantity,
                              BigDecimal marketPrice) {

        log.info("Executing {} trade: {} shares of {} for account {}",
                tradeType, quantity, symbol, accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountId));

        Portfolio portfolio = portfolioRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Portfolio not found for account: " + accountId));

        BigDecimal tradeValue = quantity.multiply(marketPrice);

        // Validate before executing
        validateTrade(portfolio, tradeType, tradeValue, quantity);

        // Update portfolio cash balance
        if (tradeType == TradeType.BUY) {
            // Buying stocks — deduct cash
            portfolio.setCashBalance(
                    portfolio.getCashBalance().subtract(tradeValue));
        } else {
            // Selling stocks — add cash
            portfolio.setCashBalance(
                    portfolio.getCashBalance().add(tradeValue));
        }

        portfolioRepository.save(portfolio);

        // Record the trade
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
        log.info("Trade executed successfully. ID: {}", saved.getId());
        return saved;
    }

    // Get all trades for an account
    public List<Trade> getTradesByAccountId(Long accountId) {
        return tradeRepository.findByAccountId(accountId);
    }

    // Get only executed trades
    public List<Trade> getExecutedTrades(Long accountId) {
        return tradeRepository.findByAccountIdAndStatus(
                accountId, TradeStatus.EXECUTED);
    }

    private void validateTrade(Portfolio portfolio, TradeType tradeType,
                               BigDecimal tradeValue, BigDecimal quantity) {

        // Quantity must be positive
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Trade quantity must be positive");
        }

        // For BUY — check sufficient cash balance
        if (tradeType == TradeType.BUY) {
            if (portfolio.getCashBalance().compareTo(tradeValue) < 0) {
                throw new RuntimeException(
                        String.format("Insufficient funds. Required: %.2f, Available: %.2f",
                                tradeValue, portfolio.getCashBalance()));
            }
        }
    }
}