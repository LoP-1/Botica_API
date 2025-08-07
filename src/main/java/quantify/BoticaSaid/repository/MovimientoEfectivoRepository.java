package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quantify.BoticaSaid.model.MovimientoEfectivo;

import java.util.List;

import quantify.BoticaSaid.model.Caja;
@Repository
public interface MovimientoEfectivoRepository extends JpaRepository<MovimientoEfectivo, Integer> {
    List<MovimientoEfectivo> findByCaja(Caja caja);
    List<MovimientoEfectivo> findByCajaAndEsManual(Caja caja, boolean esManual);
}
