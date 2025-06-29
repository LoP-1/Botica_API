package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quantify.BoticaSaid.model.Producto;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Producto findByCodigoBarras(String codigoBarras);

    List<Producto> findByNombreContainingIgnoreCase(String nombre);

    List<Producto> findByCategoriaContainingIgnoreCase(String categoria);

    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCase(String nombre, String categoria);


    List<Producto> findByActivoTrue();
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
    List<Producto> findByCategoriaContainingIgnoreCaseAndActivoTrue(String categoria);
    List<Producto> findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCaseAndActivoTrue(String nombre, String categoria);
}
