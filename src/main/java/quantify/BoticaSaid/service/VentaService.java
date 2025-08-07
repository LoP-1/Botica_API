package quantify.BoticaSaid.service;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import quantify.BoticaSaid.dto.*;
import quantify.BoticaSaid.model.*;
import quantify.BoticaSaid.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VentaService {

    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final StockRepository stockRepository;
    private final UsuarioRepository usuarioRepository;
    private final CajaRepository cajaRepository;
    private final MovimientoEfectivoRepository movimientoEfectivoRepository;

    public VentaService(ProductoRepository productoRepository,
                        BoletaRepository boletaRepository,
                        DetalleBoletaRepository detalleBoletaRepository,
                        MetodoPagoRepository metodoPagoRepository,
                        StockRepository stockRepository,
                        UsuarioRepository usuarioRepository,
                        CajaRepository cajaRepository,
                        MovimientoEfectivoRepository movimientoEfectivoRepository) {
        this.productoRepository = productoRepository;
        this.boletaRepository = boletaRepository;
        this.detalleBoletaRepository = detalleBoletaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.stockRepository = stockRepository;
        this.usuarioRepository = usuarioRepository;
        this.cajaRepository = cajaRepository;
        this.movimientoEfectivoRepository = movimientoEfectivoRepository;
    }

    // Generador de número de boleta único
    private String generarNumeroBoleta() {
        LocalDateTime ahora = LocalDateTime.now();
        String fecha = String.format("%04d%02d%02d", ahora.getYear(), ahora.getMonthValue(), ahora.getDayOfMonth());
        long secuencia = System.currentTimeMillis() % 100000; // Mejorar si quieres algo más robusto
        return "B-" + fecha + "-" + secuencia;
    }

    @Transactional
    public void registrarVenta(VentaRequestDTO ventaDTO) {
        Usuario usuario = usuarioRepository.findByDni(ventaDTO.getDniVendedor())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + ventaDTO.getDniVendedor()));

        // Validar que exista una caja abierta
        Caja cajaAbierta = cajaRepository.findCajaAbiertaPorDniUsuario(usuario.getDni())
                .orElseThrow(() -> new RuntimeException("No hay una caja abierta para este usuario"));

        MetodoPago.NombreMetodo nombreMetodo = MetodoPago.NombreMetodo.valueOf(
                ventaDTO.getMetodoPago().getNombre().toUpperCase()
        );

        double efectivo = ventaDTO.getMetodoPago().getEfectivo() != null ? ventaDTO.getMetodoPago().getEfectivo() : 0.0;
        double digital = ventaDTO.getMetodoPago().getDigital() != null ? ventaDTO.getMetodoPago().getDigital() : 0.0;
        double ingresoTotal = efectivo + digital;

        for (DetalleProductoDTO producto : ventaDTO.getProductos()) {
            if (producto.getCantidad() <= 0) {
                throw new RuntimeException("No se puede vender cantidades iguales o menores a cero para el producto con código de barras: " + producto.getCodBarras());
            }
        }
        BigDecimal totalVentaCalculado = BigDecimal.ZERO;

        //Calcular el total real de la venta antes de continuar
        for (DetalleProductoDTO item : ventaDTO.getProductos()) {
            Producto producto = productoRepository.findByCodigoBarras(item.getCodBarras());
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + item.getCodBarras());
            }
            int unidadesPorBlister = producto.getCantidadUnidadesBlister() != null ? producto.getCantidadUnidadesBlister() : 0;
            BigDecimal precioBlister = producto.getPrecioVentaBlister();
            BigDecimal precioUnidad = producto.getPrecioVentaUnd();

            int cantidadBlisters = 0;
            int unidadesSueltas = item.getCantidad();

            if (unidadesPorBlister > 0 && precioBlister != null && precioBlister.compareTo(BigDecimal.ZERO) > 0) {
                cantidadBlisters = item.getCantidad() / unidadesPorBlister;
                unidadesSueltas = item.getCantidad() % unidadesPorBlister;
            }

            totalVentaCalculado = totalVentaCalculado
                    .add(precioBlister != null ? precioBlister.multiply(BigDecimal.valueOf(cantidadBlisters)) : BigDecimal.ZERO)
                    .add(precioUnidad.multiply(BigDecimal.valueOf(unidadesSueltas)));
        }

        //Validar que la suma del pago sea suficiente
        if (BigDecimal.valueOf(ingresoTotal).compareTo(totalVentaCalculado) < 0) {
            throw new RuntimeException("El monto pagado (" + ingresoTotal +
                    ") es insuficiente para cubrir el total de la venta (" + totalVentaCalculado + ").");
        }

        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre(nombreMetodo);
        metodoPago.setEfectivo(efectivo);
        metodoPago.setDigital(digital);

        Boleta boleta = new Boleta();
        boleta.setNumero(generarNumeroBoleta());
        boleta.setDniCliente(ventaDTO.getDniCliente());
        boleta.setDniVendedor(ventaDTO.getDniVendedor());
        boleta.setFechaVenta(LocalDateTime.now());
        boleta.setTotalCompra(BigDecimal.ZERO);
        boleta.setNombreCliente(ventaDTO.getNombreCliente());
        boleta.setUsuario(usuario);
        boleta.setMetodoPago(metodoPago);
        metodoPago.setBoleta(boleta);
        System.out.println("Numero asignado a la entidad Boleta: " + boleta.getNumero());
        Boleta boletaGuardada = boletaRepository.save(boleta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleProductoDTO item : ventaDTO.getProductos()) {
            String codBarras = item.getCodBarras();
            int cantidadSolicitada = item.getCantidad();

            if (cantidadSolicitada <= 0) {
                throw new RuntimeException("No se puede vender cantidades iguales o menores a cero para el producto con código de barras: " + codBarras);
            }

            Producto producto = productoRepository.findByCodigoBarras(codBarras);
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + codBarras);
            }

            List<Stock> stocks = stockRepository.findByProductoOrderByFechaVencimientoAsc(producto);

            Integer unidadesPorBlister = producto.getCantidadUnidadesBlister();
            BigDecimal precioBlister = producto.getPrecioVentaBlister();
            BigDecimal precioUnidad = producto.getPrecioVentaUnd();

            int cantidadBlisters = 0;
            int unidadesSueltas = cantidadSolicitada;

            if (unidadesPorBlister != null && unidadesPorBlister > 0 && precioBlister != null && precioBlister.compareTo(BigDecimal.ZERO) > 0) {
                cantidadBlisters = cantidadSolicitada / unidadesPorBlister;
                unidadesSueltas = cantidadSolicitada % unidadesPorBlister;
            }

            int unidadesParaBlisters = cantidadBlisters * (unidadesPorBlister != null ? unidadesPorBlister : 0);
            int cantidadRestanteBlister = unidadesParaBlisters;

            if (cantidadBlisters > 0) {
                for (Stock stock : stocks) {
                    if (cantidadRestanteBlister == 0) break;
                    if (stock.getCantidadUnidades() == 0) continue;
                    int cantidadUsada = Math.min(stock.getCantidadUnidades(), cantidadRestanteBlister);
                    stock.setCantidadUnidades(stock.getCantidadUnidades() - cantidadUsada);
                    cantidadRestanteBlister -= cantidadUsada;
                    stockRepository.save(stock);

                    int blisterEnEsteStock = cantidadUsada / unidadesPorBlister;
                    if (blisterEnEsteStock > 0) {
                        DetalleBoleta detalleBlister = new DetalleBoleta();
                        detalleBlister.setBoleta(boletaGuardada);
                        detalleBlister.setProducto(producto);
                        detalleBlister.setCantidad(blisterEnEsteStock * unidadesPorBlister);
                        detalleBlister.setPrecioUnitario(precioBlister.setScale(2, RoundingMode.HALF_UP));
                        detalleBoletaRepository.save(detalleBlister);

                        totalVenta = totalVenta.add(precioBlister.multiply(BigDecimal.valueOf(blisterEnEsteStock)));
                    }

                    int sobrante = cantidadUsada % unidadesPorBlister;
                    if (sobrante > 0) {
                        unidadesSueltas += sobrante;
                    }
                }
            }

            int cantidadRestanteUnidad = unidadesSueltas;
            for (Stock stock : stocks) {
                if (cantidadRestanteUnidad == 0) break;
                if (stock.getCantidadUnidades() == 0) continue;
                int cantidadUsada = Math.min(stock.getCantidadUnidades(), cantidadRestanteUnidad);
                stock.setCantidadUnidades(stock.getCantidadUnidades() - cantidadUsada);
                cantidadRestanteUnidad -= cantidadUsada;
                stockRepository.save(stock);

                if (cantidadUsada > 0) {
                    DetalleBoleta detalleUnidad = new DetalleBoleta();
                    detalleUnidad.setBoleta(boletaGuardada);
                    detalleUnidad.setProducto(producto);
                    detalleUnidad.setCantidad(cantidadUsada);
                    detalleUnidad.setPrecioUnitario(precioUnidad.setScale(2, RoundingMode.HALF_UP));
                    detalleBoletaRepository.save(detalleUnidad);

                    totalVenta = totalVenta.add(precioUnidad.multiply(BigDecimal.valueOf(cantidadUsada)));
                }
            }
            if ((cantidadRestanteBlister > 0 && cantidadBlisters > 0) || cantidadRestanteUnidad > 0) {
                throw new RuntimeException("Stock insuficiente para el producto con código de barras: " + codBarras);
            }

            producto.setCantidadGeneral(producto.getCantidadGeneral() - cantidadSolicitada);
            productoRepository.save(producto);
        }

        BigDecimal vuelto = BigDecimal.valueOf(ingresoTotal).subtract(totalVenta);
        boletaGuardada.setTotalCompra(totalVenta.setScale(2, RoundingMode.HALF_UP));
        boletaGuardada.setVuelto(vuelto.setScale(2, RoundingMode.HALF_UP));
        boletaRepository.save(boletaGuardada);

        MovimientoEfectivo movimiento = new MovimientoEfectivo();
        movimiento.setCaja(cajaAbierta);
        movimiento.setTipo(MovimientoEfectivo.TipoMovimiento.INGRESO);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setMonto(totalVenta.setScale(2, RoundingMode.HALF_UP));
        movimiento.setDescripcion("Venta registrada - Boleta ID: " + boletaGuardada.getId());
        movimiento.setUsuario(usuario);
        movimiento.setEsManual(false);
        movimientoEfectivoRepository.save(movimiento);

        if (nombreMetodo == MetodoPago.NombreMetodo.EFECTIVO) {
            cajaAbierta.setEfectivoFinal(
                    (cajaAbierta.getEfectivoFinal() != null ? cajaAbierta.getEfectivoFinal() : BigDecimal.ZERO)
                            .add(BigDecimal.valueOf(efectivo))
            );
        } else if (nombreMetodo == MetodoPago.NombreMetodo.YAPE) {
            cajaAbierta.setTotalYape(
                    (cajaAbierta.getTotalYape() != null ? cajaAbierta.getTotalYape() : BigDecimal.ZERO)
                            .add(BigDecimal.valueOf(digital))
            );
        } else if (nombreMetodo == MetodoPago.NombreMetodo.MIXTO) {
            cajaAbierta.setEfectivoFinal(
                    (cajaAbierta.getEfectivoFinal() != null ? cajaAbierta.getEfectivoFinal() : BigDecimal.ZERO)
                            .add(BigDecimal.valueOf(efectivo))
            );
            cajaAbierta.setTotalYape(
                    (cajaAbierta.getTotalYape() != null ? cajaAbierta.getTotalYape() : BigDecimal.ZERO)
                            .add(BigDecimal.valueOf(digital))
            );
        }

        cajaRepository.save(cajaAbierta);
    }

    public VentaResponseDTO convertirABoletaResponseDTO(Boleta boleta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setId(boleta.getId());
        dto.setNumero(boleta.getNumero());
        dto.setFecha(boleta.getFechaVenta() != null ? boleta.getFechaVenta().toString() : null);
        dto.setCliente(boleta.getNombreCliente());
        dto.setMetodoPago(boleta.getMetodoPago() != null
                ? boleta.getMetodoPago().getNombre().toString()
                : null);
        dto.setTotal(boleta.getTotalCompra() != null ? boleta.getTotalCompra().setScale(2, RoundingMode.HALF_UP) : null);
        dto.setUsuario(boleta.getUsuario() != null ? boleta.getUsuario().getNombreCompleto() : null);

        List<DetalleProductoDTO> productos = boleta.getDetalles() != null
                ? boleta.getDetalles().stream().map(detalle -> {
            DetalleProductoDTO prodDto = new DetalleProductoDTO();
            prodDto.setCodBarras(detalle.getProducto().getCodigoBarras());
            prodDto.setNombre(detalle.getProducto().getNombre());
            prodDto.setCantidad(detalle.getCantidad());

            String codigoBarras = detalle.getProducto().getCodigoBarras() != null ? detalle.getProducto().getCodigoBarras() : "";
            Producto producto = productoRepository.findByCodigoBarras(codigoBarras);

            if (producto != null) {
                BigDecimal precio = producto.getPrecioVentaUnd();
                prodDto.setPrecio(precio != null ? precio.setScale(2, RoundingMode.HALF_UP) : null);
            } else {
                throw new RuntimeException("Producto no encontrado: " + codigoBarras);
            }

            return prodDto;
        }).collect(Collectors.toList())
                : List.of();
        dto.setProductos(productos);

        return dto;
    }

    public List<VentaResponseDTO> listarVentas() {
        List<Boleta> boletas = boletaRepository.findAll();
        return boletas.stream()
                .map(this::convertirABoletaResponseDTO)
                .collect(Collectors.toList());
    }

    // Ventas del día
    public double getVentasDelDia() {
        LocalDateTime inicioDia = LocalDateTime.now().toLocalDate().atStartOfDay();
        return boletaRepository.sumTotalCompraByFechaVentaAfter(inicioDia).orElse(0.0);
    }

    // Variación de ventas del día (respecto a ayer)
    public double getVariacionVentasDia() {
        LocalDateTime inicioHoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime inicioAyer = inicioHoy.minusDays(1);
        LocalDateTime finAyer = inicioHoy.minusSeconds(1);

        double ventasAyer = boletaRepository.sumTotalCompraByFechaVentaBetween(inicioAyer, finAyer).orElse(0.0);
        double ventasHoy = boletaRepository.sumTotalCompraByFechaVentaAfter(inicioHoy).orElse(0.0);

        if (ventasAyer == 0) return ventasHoy > 0 ? 100.0 : 0.0;
        return ((ventasHoy - ventasAyer) / ventasAyer) * 100.0;
    }

    // Ventas del mes
    public double getVentasDelMes() {
        LocalDateTime inicioMes = LocalDateTime.now().withDayOfMonth(1).toLocalDate().atStartOfDay();
        return boletaRepository.sumTotalCompraByFechaVentaAfter(inicioMes).orElse(0.0);
    }

    // Variación de ventas del mes
    public double getVariacionVentasMes() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime inicioMesActual = ahora.withDayOfMonth(1).toLocalDate().atStartOfDay();
        LocalDateTime inicioMesAnterior = inicioMesActual.minusMonths(1);
        LocalDateTime finMesAnterior = inicioMesActual.minusSeconds(1);

        double ventasMesAnterior = boletaRepository.sumTotalCompraByFechaVentaBetween(inicioMesAnterior, finMesAnterior).orElse(0.0);
        double ventasMesActual = boletaRepository.sumTotalCompraByFechaVentaAfter(inicioMesActual).orElse(0.0);

        if (ventasMesAnterior == 0) return ventasMesActual > 0 ? 100.0 : 0.0;
        return ((ventasMesActual - ventasMesAnterior) / ventasMesAnterior) * 100.0;
    }

    // Clientes atendidos hoy
    public int getClientesAtendidosHoy() {
        LocalDateTime inicioDia = LocalDateTime.now().toLocalDate().atStartOfDay();
        return boletaRepository.countDistinctDniClienteByFechaVentaAfter(inicioDia).orElse(0);
    }

    // Variación de clientes atendidos (respecto a ayer)
    public double getVariacionClientesAtendidos() {
        LocalDateTime inicioHoy = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime inicioAyer = inicioHoy.minusDays(1);
        LocalDateTime finAyer = inicioHoy.minusSeconds(1);

        int clientesAyer = boletaRepository.countDistinctDniClienteByFechaVentaBetween(inicioAyer, finAyer).orElse(0);
        int clientesHoy = boletaRepository.countDistinctDniClienteByFechaVentaAfter(inicioHoy).orElse(0);

        if (clientesAyer == 0) return clientesHoy > 0 ? 100.0 : 0.0;
        return ((double)(clientesHoy - clientesAyer) / clientesAyer) * 100.0;
    }

    // Últimas ventas (puedes ajustar el límite)
    public List<DashboardResumenDTO.VentaRecienteDTO> getUltimasVentasDTO(int limite) {
        List<Boleta> ultimas = boletaRepository.findTopNByOrderByFechaVentaDesc(PageRequest.of(0, limite));
        return ultimas.stream().map(boleta -> {
            DashboardResumenDTO.VentaRecienteDTO dto = new DashboardResumenDTO.VentaRecienteDTO();
            dto.boleta = boleta.getNumero();
            dto.cliente = boleta.getNombreCliente();
            dto.monto = boleta.getTotalCompra() != null ? boleta.getTotalCompra().doubleValue() : 0.0;
            return dto;
        }).collect(Collectors.toList());
    }

    // CORREGIDO: usa boletaRepository para ventas por hora
    public List<VentasPorHoraDTO> getVentasPorHoraUltimas24() {
        LocalDateTime hace24Horas = LocalDateTime.now().minusHours(24);
        List<Object[]> resultados = boletaRepository.obtenerVentasPorHora(hace24Horas);

        return resultados.stream().map(obj -> {
            VentasPorHoraDTO dto = new VentasPorHoraDTO();
            dto.setHora((String) obj[0]);
            dto.setTotal(((Number) obj[1]).doubleValue());
            return dto;
        }).collect(Collectors.toList());
    }
}