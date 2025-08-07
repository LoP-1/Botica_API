package quantify.BoticaSaid.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "stock")
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "cantidad_unidades")
    private int cantidadUnidades;

    @Column(name = "fecha_vencimiento")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaVencimiento; // LocalDate en lugar de Date

    @Column(name = "precio_compra")
    private BigDecimal precioCompra;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    @JsonIgnore
    private Producto producto;

    // Constructor vacío
    public Stock() {}

    // Constructor con parámetros
    public Stock(int cantidadUnidades, LocalDate fechaVencimiento, BigDecimal precioCompra) {
        this.cantidadUnidades = cantidadUnidades;
        this.fechaVencimiento = fechaVencimiento;
        this.precioCompra = precioCompra;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCantidadUnidades() {
        return cantidadUnidades;
    }

    public void setCantidadUnidades(int cantidadUnidades) {
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

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }
}