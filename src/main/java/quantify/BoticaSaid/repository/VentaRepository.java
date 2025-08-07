package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quantify.BoticaSaid.model.Boleta; // Cambia a Boleta si corresponde

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Boleta, Long> {

    @Query(
            value = """
        SELECT DATE_FORMAT(fecha_venta, '%H:00') as hora, SUM(total_compra)
        FROM boletas
        WHERE fecha_venta >= :hace24Horas
        GROUP BY hora
        ORDER BY hora
        """, nativeQuery = true
    )
    List<Object[]> obtenerVentasPorHora(@Param("hace24Horas") LocalDateTime hace24Horas);

}