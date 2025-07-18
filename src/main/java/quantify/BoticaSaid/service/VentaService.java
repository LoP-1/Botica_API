package quantify.BoticaSaid.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import quantify.BoticaSaid.dto.DetalleProductoDTO;
import quantify.BoticaSaid.dto.VentaRequestDTO;
import quantify.BoticaSaid.dto.VentaResponseDTO;
import quantify.BoticaSaid.model.*;
import quantify.BoticaSaid.repository.*;

import java.math.BigDecimal;
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

        //Calcular el total real de la venta antes de continuar
        BigDecimal totalVentaCalculado = BigDecimal.ZERO;
        for (DetalleProductoDTO item : ventaDTO.getProductos()) {
            Producto producto = productoRepository.findByCodigoBarras(item.getCodBarras());
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + item.getCodBarras());
            }
            totalVentaCalculado = totalVentaCalculado.add(
                producto.getPrecioVentaUnd().multiply(BigDecimal.valueOf(item.getCantidad()))
            );
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
        boleta.setDniCliente(ventaDTO.getDniCliente());
        boleta.setDniVendedor(ventaDTO.getDniVendedor());
        boleta.setFechaVenta(LocalDateTime.now());
        boleta.setTotalCompra(BigDecimal.ZERO);
        boleta.setNombreCliente(ventaDTO.getNombreCliente());
        boleta.setUsuario(usuario);
        boleta.setMetodoPago(metodoPago);
        metodoPago.setBoleta(boleta);

        Boleta boletaGuardada = boletaRepository.save(boleta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        for (DetalleProductoDTO item : ventaDTO.getProductos()) {
            String codBarras = item.getCodBarras();
            int cantidadSolicitada = item.getCantidad();

            // --- VALIDACIÓN EXTRA (OPCIONAL REDUNDANTE, SOLO POR SEGURIDAD) ---
            if (cantidadSolicitada <= 0) {
                throw new RuntimeException("No se puede vender cantidades iguales o menores a cero para el producto con código de barras: " + codBarras);
            }
            // ---------------------------------------------------------

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

            // Primero, descontar y registrar los blisters completos
            int unidadesParaBlisters = cantidadBlisters * (unidadesPorBlister != null ? unidadesPorBlister : 0);
            int cantidadRestanteBlister = unidadesParaBlisters;

            // Control para no pasar dos veces por stocks si no hay blisters
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
                        detalleBlister.setPrecioUnitario(precioBlister);
                        detalleBoletaRepository.save(detalleBlister);

                        totalVenta = totalVenta.add(precioBlister.multiply(BigDecimal.valueOf(blisterEnEsteStock)));
                    }

                    int sobrante = cantidadUsada % unidadesPorBlister;
                    if (sobrante > 0) {
                        unidadesSueltas += sobrante;
                    }
                }
            }

            // Luego, descontar y registrar las unidades sueltas
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
                    detalleUnidad.setPrecioUnitario(precioUnidad);
                    detalleBoletaRepository.save(detalleUnidad);

                    totalVenta = totalVenta.add(precioUnidad.multiply(BigDecimal.valueOf(cantidadUsada)));
                }
            }
            // Validación de stock insuficiente
            if ((cantidadRestanteBlister > 0 && cantidadBlisters > 0) || cantidadRestanteUnidad > 0) {
                throw new RuntimeException("Stock insuficiente para el producto con código de barras: " + codBarras);
            }

            producto.setCantidadGeneral(producto.getCantidadGeneral() - cantidadSolicitada);
            productoRepository.save(producto);
        }

        BigDecimal vuelto = BigDecimal.valueOf(ingresoTotal).subtract(totalVenta);
        boletaGuardada.setTotalCompra(totalVenta);
        boletaGuardada.setVuelto(vuelto);
        boletaRepository.save(boletaGuardada);

        // Registrar movimiento de efectivo en la caja
        MovimientoEfectivo movimiento = new MovimientoEfectivo();
        movimiento.setCaja(cajaAbierta);
        movimiento.setTipo(MovimientoEfectivo.TipoMovimiento.INGRESO);
        movimiento.setFecha(LocalDateTime.now());
        movimiento.setMonto(totalVenta);
        movimiento.setDescripcion("Venta registrada - Boleta ID: " + boletaGuardada.getId());
        movimiento.setUsuario(usuario);
        movimiento.setEsManual(false);
        movimientoEfectivoRepository.save(movimiento);

        // Actualizar totales en la caja
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
        dto.setBoleta(boleta.getNumero());
        dto.setFecha(boleta.getFechaVenta() != null ? boleta.getFechaVenta().toString() : null);
        dto.setCliente(boleta.getNombreCliente());
        dto.setMetodoPago(boleta.getMetodoPago() != null
                ? boleta.getMetodoPago().getNombre().toString()
                : null);
        dto.setTotal(boleta.getTotalCompra());
        dto.setUsuario(boleta.getUsuario() != null ? boleta.getUsuario().getNombreCompleto() : null);

        List<DetalleProductoDTO> productos = boleta.getDetalles() != null
                ? boleta.getDetalles().stream().map(detalle -> {
            DetalleProductoDTO prodDto = new DetalleProductoDTO();
            prodDto.setCodBarras(detalle.getProducto().getCodigoBarras());
            prodDto.setNombre(detalle.getProducto().getNombre());
            prodDto.setCantidad(detalle.getCantidad());

            //llamar a la base de datos y conseguir el precio del producto
            String codigoBarras = detalle.getProducto().getCodigoBarras() != null ? detalle.getProducto().getCodigoBarras() : "";
            Producto producto = productoRepository.findByCodigoBarras(codigoBarras);

            if (producto != null) {
                BigDecimal precio = producto.getPrecioVentaUnd();
                prodDto.setPrecio(precio);
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
}