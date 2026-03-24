package portfolio_service.controller;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreatePortfolioRequest {
    private Long accountId;
    private BigDecimal initialCash;
}