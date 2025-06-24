package quantify.BoticaSaid.dto;

import java.math.BigDecimal;
import java.util.List;

public class BoletaResponseDTO {
    private Long id;
    private String numero;
    private String fecha;
    private String cliente;
    private String metodoPago; // <--- AQUI
    private BigDecimal total;
    private String usuario;
    private List<DetalleProductoDTO> productos;
    private BigDecimal totalCompra;
    private BigDecimal vuelto;

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getFecha() { return fecha; }
    public void setFecha(String fecha) { this.fecha = fecha; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public List<DetalleProductoDTO> getProductos() { return productos; }
    public void setProductos(List<DetalleProductoDTO> productos) { this.productos = productos; }

    public BigDecimal getTotalCompra() { return totalCompra; }
    public void setTotalCompra(BigDecimal totalCompra) { this.totalCompra = totalCompra; }

    public BigDecimal getVuelto() { return vuelto; }
    public void setVuelto(BigDecimal vuelto) { this.vuelto = vuelto; }
}