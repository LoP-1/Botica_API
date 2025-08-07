package quantify.BoticaSaid.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StockLoteDTO {
    private Integer cantidadUnidades;
    private LocalDate fechaVencimiento;
    private BigDecimal precioCompra;

    // Getters y setters
    public Integer getCantidadUnidades() {
        return cantidadUnidades;
    }

    public void setCantidadUnidades(Integer cantidadUnidades) {
        this.cantidadUnidades = cantidadUnidades;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }
}