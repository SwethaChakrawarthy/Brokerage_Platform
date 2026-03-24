package portfolio_service.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import portfolio_service.model.Trade;
import portfolio_service.model.TradeType;
import portfolio_service.service.TradeService;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/trades")
@RequiredArgsConstructor
public class TradeController {

    private final TradeService tradeService;

    // POST /api/v1/trades/execute
    @PostMapping("/execute")
    public ResponseEntity<Trade> executeTrade(
            @RequestBody TradeRequest request) {
        Trade trade = tradeService.executeTrade(
                request.getAccountId(),
                request.getSymbol(),
                request.getTradeType(),
                request.getQuantity(),
                request.getMarketPrice()
        );
        return ResponseEntity.ok(trade);
    }

    // GET /api/v1/trades/account/1
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Trade>> getTradesByAccount(
            @PathVariable Long accountId) {
        List<Trade> trades =
                tradeService.getTradesByAccountId(accountId);
        return ResponseEntity.ok(trades);
    }

    // GET /api/v1/trades/account/1/executed
    @GetMapping("/account/{accountId}/executed")
    public ResponseEntity<List<Trade>> getExecutedTrades(
            @PathVariable Long accountId) {
        List<Trade> trades =
                tradeService.getExecutedTrades(accountId);
        return ResponseEntity.ok(trades);
    }

    @Data
    static class TradeRequest {
        private Long accountId;
        private String symbol;
        private TradeType tradeType;
        private BigDecimal quantity;
        private BigDecimal marketPrice;
    }
}
