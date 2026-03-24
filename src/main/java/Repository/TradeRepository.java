package portfolio_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import portfolio_service.model.Trade;
import portfolio_service.model.TradeStatus;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {

    List<Trade> findByAccountId(Long accountId);

    List<Trade> findByAccountIdAndStatus(Long accountId, TradeStatus status);
}
