package portfolio_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import portfolio_service.model.Account;
import portfolio_service.model.Holding;
import portfolio_service.model.Portfolio;
import portfolio_service.repository.AccountRepository;
import portfolio_service.repository.PortfolioRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;
    private final AccountRepository accountRepository;

    // Get portfolio by account ID
    public Portfolio getPortfolioByAccountId(Long accountId) {
        log.info("Fetching portfolio for account: {}", accountId);
        return portfolioRepository.findByAccountId(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Portfolio not found for account: " + accountId));
    }

    // Create a new portfolio for an account
    public Portfolio createPortfolio(Long accountId, BigDecimal initialCash) {
        log.info("Creating portfolio for account: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException(
                        "Account not found: " + accountId));

        Portfolio portfolio = Portfolio.builder()
                .account(account)
                .cashBalance(initialCash)
                .totalValue(initialCash)
                .build();

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Portfolio created with ID: {}", saved.getId());
        return saved;
    }

    // Rebalance portfolio — recalculate total value from holdings
    public Portfolio rebalancePortfolio(Long accountId) {
        log.info("Starting rebalancing for account: {}", accountId);

        Portfolio portfolio = getPortfolioByAccountId(accountId);
        List<Holding> holdings = portfolio.getHoldings();

        if (holdings == null || holdings.isEmpty()) {
            throw new RuntimeException("No holdings to rebalance");
        }

        // Calculate total market value of all holdings
        BigDecimal totalMarketValue = holdings.stream()
                .map(h -> h.getQuantity().multiply(h.getCurrentPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total portfolio = market value of stocks + cash balance
        BigDecimal totalPortfolioValue = totalMarketValue
                .add(portfolio.getCashBalance());

        portfolio.setTotalValue(totalPortfolioValue);
        portfolio.setLastRebalanced(LocalDateTime.now());

        Portfolio rebalanced = portfolioRepository.save(portfolio);
        log.info("Rebalancing complete. New total value: {}", totalPortfolioValue);
        return rebalanced;
    }

    // Get portfolio summary with gain/loss calculations
    public PortfolioSummary getPortfolioSummary(Long accountId) {
        log.info("Getting summary for account: {}", accountId);

        Portfolio portfolio = getPortfolioByAccountId(accountId);
        List<Holding> holdings = portfolio.getHoldings();

        // Calculate total gain/loss across all holdings
        BigDecimal totalGainLoss = holdings == null ? BigDecimal.ZERO :
                holdings.stream()
                        .map(h -> h.getCurrentPrice()
                                .subtract(h.getAverageCost())
                                .multiply(h.getQuantity()))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calculate return percentage
        BigDecimal returnPct = BigDecimal.ZERO;
        if (portfolio.getTotalValue().compareTo(BigDecimal.ZERO) > 0) {
            returnPct = totalGainLoss
                    .divide(portfolio.getTotalValue(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return PortfolioSummary.builder()
                .portfolioId(portfolio.getId())
                .totalValue(portfolio.getTotalValue())
                .cashBalance(portfolio.getCashBalance())
                .totalGainLoss(totalGainLoss)
                .returnPercentage(returnPct)
                .holdingCount(holdings == null ? 0 : holdings.size())
                .build();
    }

    // Get all portfolios due for rebalancing (not rebalanced in 30 days)
    public List<Portfolio> getPortfoliosDueForRebalancing() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(30);
        return portfolioRepository.findPortfoliosDueForRebalancing(cutoff);
    }
}