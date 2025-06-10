package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import quantify.BoticaSaid.model.Boleta;
import quantify.BoticaSaid.model.Usuario;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Integer> {
    List<Boleta> findByUsuarioAndFechaVentaBetween(Usuario usuario, LocalDateTime desde, LocalDateTime hasta);
}
