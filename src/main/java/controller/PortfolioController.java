package portfolio_service.controller;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio_service.model.Portfolio;
import portfolio_service.service.PortfolioService;
import portfolio_service.service.PortfolioSummary;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/portfolios")
@RequiredArgsConstructor
public class PortfolioController {

    private final PortfolioService portfolioService;

    // GET /api/v1/portfolios/account/1
    @GetMapping("/account/{accountId}")
    public ResponseEntity<Portfolio> getPortfolio(
            @PathVariable Long accountId) {
        Portfolio portfolio =
                portfolioService.getPortfolioByAccountId(accountId);
        return ResponseEntity.ok(portfolio);
    }

    // GET /api/v1/portfolios/account/1/summary
    @GetMapping("/account/{accountId}/summary")
    public ResponseEntity<PortfolioSummary> getPortfolioSummary(
            @PathVariable Long accountId) {
        PortfolioSummary summary =
                portfolioService.getPortfolioSummary(accountId);
        return ResponseEntity.ok(summary);
    }

    // POST /api/v1/portfolios
    // Body: { "accountId": 1, "initialCash": 50000.00 }
    @PostMapping
    public ResponseEntity<Portfolio> createPortfolio(
            @RequestBody portfolio_service.controller.CreatePortfolioRequest request) {
        Portfolio portfolio = portfolioService.createPortfolio(
                request.getAccountId(), request.getInitialCash());
        return ResponseEntity
                .status(HttpStatus.CREATED).body(portfolio);
    }

    // PUT /api/v1/portfolios/account/1/rebalance
    @PreAuthorize("hasAnyRole('ADVISOR', 'ADMIN')")
    @PutMapping("/account/{accountId}/rebalance")
    public ResponseEntity<Portfolio> rebalancePortfolio(
            @PathVariable Long accountId) {
        Portfolio rebalanced =
                portfolioService.rebalancePortfolio(accountId);
        return ResponseEntity.ok(rebalanced);
    }

    // GET /api/v1/portfolios/due-for-rebalancing
    @GetMapping("/due-for-rebalancing")
    public ResponseEntity<List<Portfolio>> getDueForRebalancing() {
        List<Portfolio> portfolios =
                portfolioService.getPortfoliosDueForRebalancing();
        return ResponseEntity.ok(portfolios);
    }
}