package quantify.BoticaSaid.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import quantify.BoticaSaid.dto.DetalleProductoDTO;
import quantify.BoticaSaid.dto.VentaRequestDTO;
import quantify.BoticaSaid.model.*;
import quantify.BoticaSaid.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VentaService {

    private final ProductoRepository productoRepository;
    private final BoletaRepository boletaRepository;
    private final DetalleBoletaRepository detalleBoletaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final StockRepository stockRepository;
    private final UsuarioRepository usuarioRepository;

    public VentaService(ProductoRepository productoRepository,
                        BoletaRepository boletaRepository,
                        DetalleBoletaRepository detalleBoletaRepository,
                        MetodoPagoRepository metodoPagoRepository,
                        StockRepository stockRepository,
                        UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.boletaRepository = boletaRepository;
        this.detalleBoletaRepository = detalleBoletaRepository;
        this.metodoPagoRepository = metodoPagoRepository;
        this.stockRepository = stockRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void registrarVenta(VentaRequestDTO ventaDTO) {
        // Buscar usuario (vendedor) por DNI
        Usuario usuario = usuarioRepository.findByDni(ventaDTO.getDniVendedor())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con DNI: " + ventaDTO.getDniVendedor()));

        // Convertir el nombre del método de pago a enum
        MetodoPago.NombreMetodo nombreMetodo = MetodoPago.NombreMetodo.valueOf(
                ventaDTO.getMetodoPago().getNombre().toUpperCase()
        );

        // Calcular ingreso y vuelto
        double efectivo = ventaDTO.getMetodoPago().getEfectivo() != null ? ventaDTO.getMetodoPago().getEfectivo() : 0.0;
        double digital = ventaDTO.getMetodoPago().getDigital() != null ? ventaDTO.getMetodoPago().getDigital() : 0.0;
        double ingresoTotal = efectivo + digital;

        // Crear método de pago (se guarda primero para asociarlo a la boleta)
        MetodoPago metodoPago = new MetodoPago();
        metodoPago.setNombre(nombreMetodo);
        metodoPago.setEfectivo(efectivo);
        metodoPago.setDigital(digital);
        metodoPagoRepository.save(metodoPago);

        // Crear boleta con método de pago y usuario ya asignados
        Boleta boleta = new Boleta();
        boleta.setDniCliente(ventaDTO.getDniCliente());
        boleta.setDniVendedor(ventaDTO.getDniVendedor());
        boleta.setFechaVenta(LocalDateTime.now());
        boleta.setTotalCompra(BigDecimal.ZERO); // Se actualiza después
        boleta.setNombreCliente(ventaDTO.getNombreCliente());
        boleta.setUsuario(usuario);
        boleta.setMetodoPago(metodoPago); // ya asignado
        boleta.setVuelto(BigDecimal.ZERO); // temporalmente
        Boleta boletaGuardada = boletaRepository.save(boleta);

        BigDecimal totalVenta = BigDecimal.ZERO;

        // Procesar productos con lógica FIFO
        for (DetalleProductoDTO item : ventaDTO.getProductos()) {
            String codBarras = item.getCodBarras();
            int cantidadSolicitada = item.getCantidad();

            Producto producto = productoRepository.findByCodigoBarras(codBarras);
            if (producto == null) {
                throw new RuntimeException("Producto no encontrado: " + codBarras);
            }

            List<Stock> stocks = stockRepository.findByProductoOrderByFechaVencimientoAsc(producto);
            int cantidadRestante = cantidadSolicitada;

            for (Stock stock : stocks) {
                if (stock.getCantidadUnidades() == 0) continue;

                int cantidadUsada = Math.min(stock.getCantidadUnidades(), cantidadRestante);
                stock.setCantidadUnidades(stock.getCantidadUnidades() - cantidadUsada);
                cantidadRestante -= cantidadUsada;
                stockRepository.save(stock);

                // Detalle de boleta
                DetalleBoleta detalle = new DetalleBoleta();
                detalle.setBoleta(boletaGuardada);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidadUsada);
                detalle.setPrecioUnitario(producto.getPrecioVentaUnd());
                detalleBoletaRepository.save(detalle);

                totalVenta = totalVenta.add(
                        producto.getPrecioVentaUnd().multiply(BigDecimal.valueOf(cantidadUsada))
                );

                if (cantidadRestante == 0) break;
            }

            if (cantidadRestante > 0) {
                throw new RuntimeException("Stock insuficiente para el producto con código de barras: " + codBarras);
            }

            producto.setCantidadGeneral(producto.getCantidadGeneral() - cantidadSolicitada);
            productoRepository.save(producto);
        }

        // Actualizar total y vuelto
        BigDecimal vuelto = BigDecimal.valueOf(ingresoTotal).subtract(totalVenta);
        boletaGuardada.setTotalCompra(totalVenta);
        boletaGuardada.setVuelto(vuelto);
        boletaRepository.save(boletaGuardada);
    }
}
