package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import quantify.BoticaSaid.model.Producto;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // ✅ NUEVA: Consulta que incluye stocks
    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN FETCH p.stocks WHERE p.activo = true")
    List<Producto> findByActivoTrueWithStocks();

    // ✅ NUEVA: Buscar por código con stocks
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.stocks WHERE p.codigoBarras = :codigoBarras")
    Optional<Producto> findByCodigoBarrasWithStocks(@Param("codigoBarras") String codigoBarras);

    // Mantener las consultas existentes como fallback
    Producto findByCodigoBarras(String codigoBarras);
    List<Producto> findByActivoTrue();
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Producto> findByCategoriaContainingIgnoreCaseAndActivoTrue(String categoria);
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCaseAndActivoTrue(String nombre, String categoria);

    @Query(
            value = """
            SELECT p.nombre, SUM(d.cantidad) AS totalVendidas,
              (SUM(d.cantidad) * 100.0 / (SELECT SUM(d2.cantidad) FROM detalles_boleta d2)) AS porcentaje
            FROM detalles_boleta d
            JOIN productos p ON d.codigo_barras = p.codigo_barras
            GROUP BY p.codigo_barras, p.nombre
            ORDER BY totalVendidas DESC
            """,
            nativeQuery = true
    )
    List<Object[]> findProductosMasVendidos(Pageable pageable);

}