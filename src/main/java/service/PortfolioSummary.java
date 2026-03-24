package portfolio_service.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioSummary {

    private Long portfolioId;
    private BigDecimal totalValue;
    private BigDecimal cashBalance;
    private BigDecimal totalGainLoss;
    private BigDecimal returnPercentage;
    private int holdingCount;
}