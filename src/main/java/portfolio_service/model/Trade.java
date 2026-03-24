package portfolio_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "trades")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    private TradeType tradeType;

    @Column(nullable = false)
    private BigDecimal quantity;

    @Column(name = "execution_price", precision = 15, scale = 2)
    private BigDecimal executionPrice;

    @Enumerated(EnumType.STRING)
    private TradeStatus status;

    @Column(name = "executed_at")
    private LocalDateTime executedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.status = TradeStatus.PENDING;
    }
}