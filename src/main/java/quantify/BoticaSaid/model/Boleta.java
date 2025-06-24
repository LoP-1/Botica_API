package quantify.BoticaSaid.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "boletas")
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero")
    private String numero;

    @Column(name = "fecha_venta")
    private LocalDateTime fechaVenta;

    @Column(name = "nombre_cliente")
    private String nombreCliente;

    @Column(name = "dni_cliente")
    private String dniCliente;

    @Column(name = "dni_vendedor")
    private String dniVendedor;

    @Column(name = "total_compra")
    private BigDecimal totalCompra;

    @Column(name = "vuelto")
    private BigDecimal vuelto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Relación con los detalles de la boleta (productos vendidos)
    @OneToMany(mappedBy = "boleta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleBoleta> detalles = new ArrayList<>();

    // Relación opcional con método de pago, si aplica en tu modelo
    @OneToOne(mappedBy = "boleta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MetodoPago metodoPago;

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }
    public void setNumero(String numero) {
        this.numero = numero;
    }

    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
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

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }
    public void setTotalCompra(BigDecimal totalCompra) {
        this.totalCompra = totalCompra;
    }

    public BigDecimal getVuelto() {
        return vuelto;
    }
    public void setVuelto(BigDecimal vuelto) {
        this.vuelto = vuelto;
    }

    public Usuario getUsuario() {
        return usuario;
    }
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<DetalleBoleta> getDetalles() {
        return detalles;
    }
    public void setDetalles(List<DetalleBoleta> detalles) {
        this.detalles = detalles;
    }

    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    public void setMetodoPago(MetodoPago metodoPago) {
        this.metodoPago = metodoPago;
    }
}