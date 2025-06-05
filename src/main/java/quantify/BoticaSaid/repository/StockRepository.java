package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quantify.BoticaSaid.model.Stock;

public interface StockRepository extends JpaRepository<Stock, Integer> {
}
