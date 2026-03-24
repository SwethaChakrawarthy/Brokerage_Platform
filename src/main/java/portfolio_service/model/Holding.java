package portfolio_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "holdings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    private Portfolio portfolio;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "average_cost", precision = 15, scale = 2)
    private BigDecimal averageCost;

    @Column(name = "current_price", precision = 15, scale = 2)
    private BigDecimal currentPrice;

    @Transient
    public BigDecimal getMarketValue() {
        return quantity.multiply(currentPrice);
    }
}