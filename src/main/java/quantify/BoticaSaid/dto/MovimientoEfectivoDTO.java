package quantify.BoticaSaid.dto;

public class MovimientoEfectivoDTO {
    private String dniUsuario;
    private String tipo; // "INGRESO" o "EGRESO"
    private Double monto;
    private String descripcion;

    public MovimientoEfectivoDTO() {
    }

    public MovimientoEfectivoDTO(String dniUsuario, String tipo, Double monto, String descripcion) {
        this.dniUsuario = dniUsuario;
        this.tipo = tipo;
        this.monto = monto;
        this.descripcion = descripcion;
    }

    public String getDniUsuario() {
        return dniUsuario;
    }

    public void setDniUsuario(String dniUsuario) {
        this.dniUsuario = dniUsuario;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}