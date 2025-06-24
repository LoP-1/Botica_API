package quantify.BoticaSaid.dto;

import java.math.BigDecimal;

public class MovimientoDTO {
    private Long id;
    private String fecha; // Puedes usar String o LocalDateTime, según tu preferencia
    private String tipo; // "ingreso" o "egreso"
    private String descripcion;
    private BigDecimal monto;
    private String usuario; // nombre del usuario responsable

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}