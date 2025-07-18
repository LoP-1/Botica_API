package quantify.BoticaSaid.dto;

import java.math.BigDecimal;
import java.util.List;

public class VentaResponseDTO {
    private Long id;
    private String boleta;
    private String fecha;        // O usa LocalDateTime si prefieres
    private String cliente;
    private String metodoPago;
    private BigDecimal total;    // Mejor que Double para dinero
    private String usuario;
    private List<DetalleProductoDTO> productos; // <--- Agrega esto

    // Getters y setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getBoleta() {
        return boleta;
    }
    public void setBoleta(String boleta) {
        this.boleta = boleta;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCliente() {
        return cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public List<DetalleProductoDTO> getProductos() {
        return productos;
    }
    public void setProductos(List<DetalleProductoDTO> productos) {
        this.productos = productos;
    }
}