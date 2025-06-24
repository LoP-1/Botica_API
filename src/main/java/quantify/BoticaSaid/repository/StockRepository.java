package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.model.Producto;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Integer> {

    List<Stock> findByProductoOrderByFechaVencimientoAsc(Producto producto);

    // Este método es clave para tu servicio y frontend:
    @Query("SELECT s FROM Stock s JOIN FETCH s.producto")
    List<Stock> findAllWithProducto();

}