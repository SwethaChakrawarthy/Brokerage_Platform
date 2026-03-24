package portfolio_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import portfolio_service.model.Portfolio;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    Optional<Portfolio> findByAccountId(Long accountId);

    @Query("SELECT p FROM Portfolio p WHERE p.lastRebalanced < :cutoffDate OR p.lastRebalanced IS NULL")
    List<Portfolio> findPortfoliosDueForRebalancing(@Param("cutoffDate") LocalDateTime cutoffDate);
}