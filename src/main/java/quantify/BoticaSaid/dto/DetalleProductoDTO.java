package quantify.BoticaSaid.dto;

import java.math.BigDecimal;

public class DetalleProductoDTO {
    private String codBarras;
    private String nombre;          // ← Nuevo
    private int cantidad;
    private BigDecimal precio;      // ← Nuevo (precio unitario)
    // private BigDecimal subtotal; // ← Opcional: cantidad * precio

    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    // public BigDecimal getSubtotal() {
    //     return subtotal;
    // }
    // public void setSubtotal(BigDecimal subtotal) {
    //     this.subtotal = subtotal;
    // }
}