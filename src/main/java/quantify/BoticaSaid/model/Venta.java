package quantify.BoticaSaid.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "monto_total")
    private Double montoTotal;

    // Si tienes más columnas, agrégalas aquí

    // Constructores
    public Venta() {}

    public Venta(LocalDateTime fechaHora, Double montoTotal) {
        this.fechaHora = fechaHora;
        this.montoTotal = montoTotal;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(Double montoTotal) {
        this.montoTotal = montoTotal;
    }

}