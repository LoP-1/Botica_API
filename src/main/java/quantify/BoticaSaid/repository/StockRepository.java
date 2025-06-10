package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.model.Producto;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    List<Stock> findByProductoOrderByFechaVencimientoAsc(Producto producto);

}
