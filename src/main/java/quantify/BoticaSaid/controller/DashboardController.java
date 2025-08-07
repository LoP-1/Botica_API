package quantify.BoticaSaid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quantify.BoticaSaid.dto.DashboardResumenDTO;
import quantify.BoticaSaid.dto.VentasPorHoraDTO;
import quantify.BoticaSaid.service.CajaService;
import quantify.BoticaSaid.service.ProductoService;
import quantify.BoticaSaid.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private VentaService ventaService;
    @Autowired
    private CajaService cajaService;
    @Autowired
    private ProductoService productoService;

    // Solo ADMINISTRADOR puede acceder a este endpoint
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/resumen")
    public DashboardResumenDTO getResumen() {
        DashboardResumenDTO resumen = new DashboardResumenDTO();

        // Ventas del día y variación
        DashboardResumenDTO.VentasDiaDTO ventasDia = new DashboardResumenDTO.VentasDiaDTO();
        ventasDia.monto = ventaService.getVentasDelDia();
        ventasDia.variacion = ventaService.getVariacionVentasDia();
        resumen.ventasDia = ventasDia;

        // Ventas del mes y variación
        DashboardResumenDTO.VentasMesDTO ventasMes = new DashboardResumenDTO.VentasMesDTO();
        ventasMes.monto = ventaService.getVentasDelMes();
        ventasMes.variacion = ventaService.getVariacionVentasMes();
        resumen.ventasMes = ventasMes;

        // Saldo de caja
        resumen.saldoCaja = cajaService.getSaldoActual();

        // Clientes atendidos y variación
        DashboardResumenDTO.ClientesAtendidosDTO clientesAtendidos = new DashboardResumenDTO.ClientesAtendidosDTO();
        clientesAtendidos.cantidad = ventaService.getClientesAtendidosHoy();
        clientesAtendidos.variacion = ventaService.getVariacionClientesAtendidos();
        resumen.clientesAtendidos = clientesAtendidos;

        // Últimas ventas
        resumen.ultimasVentas = ventaService.getUltimasVentasDTO(5);

        // Productos más vendidos
        resumen.productosMasVendidos = productoService.getProductosMasVendidosDTO(5);

        // Productos críticos (bajo stock)
        resumen.productosCriticos = productoService.getProductosCriticosDTO();

        // Productos próximos a vencer
        resumen.productosVencimiento = productoService.getProductosPorVencerDTO();

        return resumen;
    }

    @GetMapping("/ventas-por-hora")
    public List<VentasPorHoraDTO> ventasPorHora() {
        return ventaService.getVentasPorHoraUltimas24();
    }
}