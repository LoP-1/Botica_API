package quantify.BoticaSaid.dto;

import quantify.BoticaSaid.model.MetodoPago;
import java.math.BigDecimal;
import java.util.List;

public class VentaRequestDTO {
    private String numero; // <--- AGREGADO
    private String dniCliente;
    private String dniVendedor;
    private String nombreCliente;
    private MetodoPagoDTO metodoPago;
    private List<DetalleProductoDTO> productos;

    // Getter y Setter para 'numero'
    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public MetodoPagoDTO getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(MetodoPagoDTO metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public String getDniVendedor() {
        return dniVendedor;
    }

    public void setDniVendedor(String dniVendedor) {
        this.dniVendedor = dniVendedor;
    }

    public List<DetalleProductoDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<DetalleProductoDTO> productos) {
        this.productos = productos;
    }
}