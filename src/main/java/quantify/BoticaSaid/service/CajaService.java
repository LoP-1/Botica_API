package quantify.BoticaSaid.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quantify.BoticaSaid.dto.*;
import quantify.BoticaSaid.model.*;
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

        // SOLO movimientos manuales (NO ventas automáticas)
        List<MovimientoEfectivo> movimientos = movimientoEfectivoRepository.findByCajaAndEsManual(caja, true);

        BigDecimal ingresos = movimientos.stream()
                .filter(m -> m.getTipo() == MovimientoEfectivo.TipoMovimiento.INGRESO)
                .map(MovimientoEfectivo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientos.stream()
                .filter(m -> m.getTipo() == MovimientoEfectivo.TipoMovimiento.EGRESO)
                .map(MovimientoEfectivo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Obtener boletas del periodo de caja
        LocalDateTime desde = caja.getFechaApertura();
        LocalDateTime hasta = LocalDateTime.now();

        List<Boleta> boletas = boletaRepository.findByUsuarioAndFechaVentaBetween(usuario, desde, hasta);

        // Sumar ventas por método de pago -- SOLO EFECTIVO Y YAPE (NO PLIN)
        BigDecimal ventasEfectivo = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.EFECTIVO)
                .map(Boleta::getTotalCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // MIXTO: separar en efectivo y digital
        BigDecimal ventasMixtoEfectivo = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.MIXTO)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getEfectivo()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasMixtoDigital = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.MIXTO)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getDigital()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasYape = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.YAPE)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getDigital()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total ventas en efectivo y digital
        BigDecimal totalVentasEfectivo = ventasEfectivo.add(ventasMixtoEfectivo);
        BigDecimal totalVentasDigital = ventasYape.add(ventasMixtoDigital);
        BigDecimal totalVentas = totalVentasEfectivo.add(totalVentasDigital);

        // Efectivo esperado en caja al cierre: inicial + ingresos + ventas efectivo (incl. mixto efectivo) - egresos
        BigDecimal totalEfectivoEsperado = safe(caja.getEfectivoInicial())
                .add(ingresos)
                .add(totalVentasEfectivo)
                .subtract(egresos);

        // Calcular diferencia entre lo declarado y lo esperado
        BigDecimal efectivoFinal = safe(cierreCajaDTO.getEfectivoFinalDeclarado());
        BigDecimal diferencia = efectivoFinal.subtract(totalEfectivoEsperado);

        // Total digital (para info, no afecta total de caja física)
        BigDecimal totalDigital = totalVentasDigital; // solo Yape + mixto digital

        // Actualizar la caja
        caja.setFechaCierre(hasta);
        caja.setEfectivoFinal(efectivoFinal);
        caja.setTotalYape(totalDigital);
        caja.setDiferencia(diferencia);

        return cajaRepository.save(caja);
    }

    public Caja obtenerCajaAbiertaPorUsuario(String dniUsuario) {
        Usuario usuario = usuarioRepository.findByDni(dniUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + dniUsuario));
        return cajaRepository.findByUsuarioAndFechaCierreIsNull(usuario)
                .orElse(null);
    }

    // Método utilitario para proteger BigDecimal de null
    private static BigDecimal safe(BigDecimal val) {
        return val == null ? BigDecimal.ZERO : val;
    }

    // SOLO movimientos manuales para ingresos y egresos
    public CajaResumenDTO convertirCajaAResumen(Caja caja) {
        // Solo movimientos manuales
        List<MovimientoEfectivo> movimientosManual = movimientoEfectivoRepository.findByCajaAndEsManual(caja, true);

        BigDecimal ingresos = movimientosManual.stream()
                .filter(m -> m.getTipo() == MovimientoEfectivo.TipoMovimiento.INGRESO)
                .map(MovimientoEfectivo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal egresos = movimientosManual.stream()
                .filter(m -> m.getTipo() == MovimientoEfectivo.TipoMovimiento.EGRESO)
                .map(MovimientoEfectivo::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Boletas (ventas) asociadas a la caja y su usuario durante la apertura y cierre
        LocalDateTime desde = caja.getFechaApertura();
        LocalDateTime hasta = caja.getFechaCierre() != null ? caja.getFechaCierre() : LocalDateTime.now();

        List<Boleta> boletas = boletaRepository.findByUsuarioAndFechaVentaBetween(
                caja.getUsuario(), desde, hasta);

        BigDecimal ventasEfectivo = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.EFECTIVO)
                .map(Boleta::getTotalCompra)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // MIXTO: separar en efectivo y digital
        BigDecimal ventasMixtoEfectivo = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.MIXTO)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getEfectivo()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasMixtoDigital = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.MIXTO)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getDigital()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal ventasYape = boletas.stream()
                .filter(b -> b.getMetodoPago() != null && b.getMetodoPago().getNombre() == MetodoPago.NombreMetodo.YAPE)
                .map(b -> BigDecimal.valueOf(b.getMetodoPago().getDigital()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalVentasEfectivo = ventasEfectivo.add(ventasMixtoEfectivo);
        BigDecimal totalVentasDigital = ventasYape.add(ventasMixtoDigital);
        BigDecimal totalVentas = totalVentasEfectivo.add(totalVentasDigital);

        // Efectivo en caja: inicial + ingresos + totalVentasEfectivo - egresos
        BigDecimal efectivo = safe(caja.getEfectivoInicial())
                .add(ingresos)
                .add(totalVentasEfectivo)
                .subtract(egresos);

        // Calcular totalYape como suma de Yape puro y mixto digital (nunca null)
        BigDecimal totalYape = ventasYape.add(ventasMixtoDigital);

        // Mapear solo movimientos manuales a DTO
        List<MovimientoDTO> movimientosDTO = movimientosManual.stream().map(m -> {
            MovimientoDTO dto = new MovimientoDTO();
            dto.setId(m.getId().longValue());
            dto.setFecha(m.getFecha().toString());
            dto.setTipo(m.getTipo().toString().toLowerCase());
            dto.setDescripcion(m.getDescripcion());
            dto.setMonto(m.getMonto());
            dto.setUsuario(m.getUsuario().getNombreCompleto());
            return dto;
        }).toList();

        // Armar DTO de respuesta
        CajaResumenDTO dto = new CajaResumenDTO();
        dto.setEfectivoInicial(caja.getEfectivoInicial());
        dto.setEfectivoFinal(caja.getEfectivoFinal());
        dto.setIngresos(ingresos);
        dto.setEgresos(egresos);
        dto.setVentasEfectivo(totalVentasEfectivo); // sumando EFECTIVO + MIXTO efectivo
        dto.setVentasYape(ventasYape); // solo YAPE puro
        dto.setVentasPlin(BigDecimal.ZERO);
        dto.setVentasMixto(ventasMixtoDigital); // solo la parte digital de MIXTO
        dto.setTotalVentas(totalVentas);
        dto.setEfectivo(efectivo);
        dto.setTotalYape(totalYape != null ? totalYape : BigDecimal.ZERO); // <-- nunca null
        dto.setMovimientos(movimientosDTO);
        dto.setDiferencia(caja.getDiferencia());
        dto.setCajaAbierta(caja.getFechaCierre() == null);
        dto.setFechaApertura(caja.getFechaApertura() != null ? caja.getFechaApertura().toString() : null);
        dto.setFechaCierre(caja.getFechaCierre() != null ? caja.getFechaCierre().toString() : null);
        dto.setUsuarioResponsable(
                caja.getUsuario() != null ? caja.getUsuario().getNombreCompleto() : null
        );
        return dto;
    }

    @Transactional(readOnly = true)
    public List<CajaResumenDTO> obtenerHistorialCajas() {
        List<Caja> cajas = cajaRepository.findAll();
        return cajas.stream()
                .map(this::convertirCajaAResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<CajaResumenDTO> obtenerCajasAbiertas() {
        List<Caja> abiertas = cajaRepository.findByFechaCierreIsNull();
        return abiertas.stream()
                .map(this::convertirCajaAResumen)
                .toList();
    }

    @Transactional(readOnly = true)
    public CajaResumenDTO obtenerResumenCajaActual(String dniUsuario) {
        Usuario usuario = usuarioRepository.findByDni(dniUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + dniUsuario));

        Caja caja = cajaRepository.findByUsuarioAndFechaCierreIsNull(usuario)
                .orElse(null);

        if (caja == null) return null;

        return convertirCajaAResumen(caja);
    }

    public void registrarMovimientoManual(MovimientoEfectivoDTO dto) {
        Usuario usuario = usuarioRepository.findByDni(dto.getDniUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + dto.getDniUsuario()));

        Caja caja = cajaRepository.findByUsuarioAndFechaCierreIsNull(usuario)
                .orElseThrow(() -> new RuntimeException("No hay caja abierta para el usuario."));

        MovimientoEfectivo movimiento = new MovimientoEfectivo();
        movimiento.setCaja(caja);
        movimiento.setTipo(MovimientoEfectivo.TipoMovimiento.valueOf(dto.getTipo()));
        movimiento.setMonto(BigDecimal.valueOf(dto.getMonto()));
        movimiento.setDescripcion(dto.getDescripcion());
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setUsuario(usuario);
        movimiento.setEsManual(true); // CLAVE: es manual
        movimientoEfectivoRepository.save(movimiento);
    }

}