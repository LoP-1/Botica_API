package quantify.BoticaSaid.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quantify.BoticaSaid.dto.CajaAperturaDTO;
import quantify.BoticaSaid.dto.CierreCajaDTO;
import quantify.BoticaSaid.model.Boleta;
import quantify.BoticaSaid.model.Caja;
import quantify.BoticaSaid.model.MovimientoEfectivo;
import quantify.BoticaSaid.model.Usuario;
import quantify.BoticaSaid.repository.BoletaRepository;
import quantify.BoticaSaid.repository.CajaRepository;
import quantify.BoticaSaid.repository.MovimientoEfectivoRepository;
import quantify.BoticaSaid.repository.UsuarioRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CajaService {

    private final CajaRepository cajaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimientoEfectivoRepository movimientoEfectivoRepository;
    private final BoletaRepository boletaRepository;

    public CajaService(
            CajaRepository cajaRepository,
            UsuarioRepository usuarioRepository,
            MovimientoEfectivoRepository movimientoEfectivoRepository,
            BoletaRepository boletaRepository
    ) {
        this.cajaRepository = cajaRepository;
        this.usuarioRepository = usuarioRepository;
        this.movimientoEfectivoRepository = movimientoEfectivoRepository;
        this.boletaRepository = boletaRepository;
    }

    @Transactional
    public Caja abrirCaja(CajaAperturaDTO dto) {
        Usuario usuario = usuarioRepository.findByDni(dto.getDniUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + dto.getDniUsuario()));

        boolean existeCajaAbierta = cajaRepository.existsByUsuarioAndFechaCierreIsNull(usuario);
        if (existeCajaAbierta) {
            throw new RuntimeException("Ya existe una caja abierta para este usuario.");
        }

        Caja caja = new Caja();
        caja.setUsuario(usuario);
        caja.setFechaApertura(LocalDateTime.now());
        caja.setEfectivoInicial(dto.getEfectivoInicial());
        caja.setEfectivoFinal(null);
        caja.setTotalYape(null);
        caja.setDiferencia(null);

        cajaRepository.save(caja);
        return caja;
    }

    @Transactional
    public Caja cerrarCaja(CierreCajaDTO cierreCajaDTO) {
        Usuario usuario = usuarioRepository.findByDni(cierreCajaDTO.getDniUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + cierreCajaDTO.getDniUsuario()));

        Caja caja = cajaRepository.findByUsuarioAndFechaCierreIsNull(usuario)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta para el usuario."));

        // Obtener movimientos de efectivo
        List<MovimientoEfectivo> movimientos = movimientoEfectivoRepository.findByCaja(caja);

        BigDecimal totalEfectivo = movimientos.stream()
                .map(m -> m.getTipo() == MovimientoEfectivo.TipoMovimiento.INGRESO ? m.getMonto() : m.getMonto().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener total digital (yape, plin, etc.)
        LocalDateTime desde = caja.getFechaApertura();
        LocalDateTime hasta = LocalDateTime.now();

        List<Boleta> boletas = boletaRepository.findByUsuarioAndFechaVentaBetween(usuario, desde, hasta);
        BigDecimal totalDigital = boletas.stream()
                .map(b -> b.getMetodoPago().getDigital())
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Calcular diferencia
        BigDecimal efectivoFinal = cierreCajaDTO.getEfectivoFinalDeclarado();
        BigDecimal diferencia = efectivoFinal.subtract(totalEfectivo);

        // Actualizar la caja
        caja.setFechaCierre(hasta);
        caja.setEfectivoFinal(efectivoFinal);
        caja.setTotalYape(totalDigital);
        caja.setDiferencia(diferencia);

        return cajaRepository.save(caja);
    }

}
