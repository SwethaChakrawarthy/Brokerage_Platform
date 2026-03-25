package portfolio_service.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio_service.model.Portfolio;
import portfolio_service.service.AiService;
import portfolio_service.service.PortfolioService;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;
    private final PortfolioService portfolioService;

    // POST /api/v1/ai/portfolio-summary
    // Generates AI summary for a portfolio
    @PostMapping("/portfolio-summary")
    public ResponseEntity<SummaryResponse> generateSummary(
            @RequestBody SummaryRequest request) {

        log.info("AI summary requested for account: {}", request.getAccountId());

        Portfolio portfolio = portfolioService
                .getPortfolioByAccountId(request.getAccountId());

        String summary = aiService.generatePortfolioSummary(portfolio);

        return ResponseEntity.ok(new SummaryResponse(
                request.getAccountId(),
                portfolio.getTotalValue().toString(),
                summary
        ));
    }

    @Data
    static class SummaryRequest {
        private Long accountId;
    }

    @Data
    static class SummaryResponse {
        private final Long accountId;
        private final String totalValue;
        private final String summary;
    }
}