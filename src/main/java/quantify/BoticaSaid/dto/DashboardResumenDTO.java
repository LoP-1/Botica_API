package quantify.BoticaSaid.dto;

import java.util.List;

public class DashboardResumenDTO {
    public VentasDiaDTO ventasDia;
    public VentasMesDTO ventasMes;
    public SaldoCajaDTO saldoCaja;
    public ClientesAtendidosDTO clientesAtendidos;
    public List<VentaRecienteDTO> ultimasVentas;
    public List<ProductoMasVendidoDTO> productosMasVendidos;
    public List<ProductoCriticoDTO> productosCriticos;
    public List<ProductoVencimientoDTO> productosVencimiento;

    // DTO para Ventas del Día
    public static class VentasDiaDTO {
        public double monto;
        public double variacion;
    }

    // DTO para Ventas del Mes
    public static class VentasMesDTO {
        public double monto;
        public double variacion;
    }

    // DTO para Saldo de Caja
    public static class SaldoCajaDTO {
        public double total;
        public double efectivo;
        public double yape;
    }

    // DTO para Clientes Atendidos
    public static class ClientesAtendidosDTO {
        public int cantidad;
        public double variacion;
    }

    // DTO para Venta Reciente
    public static class VentaRecienteDTO {
        public String boleta;
        public String cliente;
        public double monto;
    }

    // DTO para Producto Más Vendido
    public static class ProductoMasVendidoDTO {
        public String nombre;
        public int unidades;
        public double porcentaje;
    }

    // DTO para Producto Crítico (bajo stock)
    public static class ProductoCriticoDTO {
        public String nombre;
        public int stock;
    }

    // DTO para Producto Próximo a Vencer
    public static class ProductoVencimientoDTO {
        public String nombre;
        public int dias;
    }
}