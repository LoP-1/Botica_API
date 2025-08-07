package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import quantify.BoticaSaid.model.Boleta;
import quantify.BoticaSaid.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Integer>, JpaSpecificationExecutor<Boleta> {
    List<Boleta> findByUsuarioAndFechaVentaBetween(Usuario usuario, LocalDateTime desde, LocalDateTime hasta);

    @Query("SELECT SUM(b.totalCompra) FROM Boleta b WHERE b.fechaVenta >= :fecha")
    Optional<Double> sumTotalCompraByFechaVentaAfter(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT SUM(b.totalCompra) FROM Boleta b WHERE b.fechaVenta BETWEEN :desde AND :hasta")
    Optional<Double> sumTotalCompraByFechaVentaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT COUNT(DISTINCT b.dniCliente) FROM Boleta b WHERE b.fechaVenta >= :fecha")
    Optional<Integer> countDistinctDniClienteByFechaVentaAfter(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT COUNT(DISTINCT b.dniCliente) FROM Boleta b WHERE b.fechaVenta BETWEEN :desde AND :hasta")
    Optional<Integer> countDistinctDniClienteByFechaVentaBetween(@Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta);

    @Query("SELECT b FROM Boleta b ORDER BY b.fechaVenta DESC")
    List<Boleta> findTopNByOrderByFechaVentaDesc(Pageable pageable);

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