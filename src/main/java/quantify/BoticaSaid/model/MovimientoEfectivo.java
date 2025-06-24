package quantify.BoticaSaid.model; // Asegúrate de que el paquete sea el correcto

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_efectivo")
public class MovimientoEfectivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // El diagrama muestra INT(11)

    // Relación con Caja: un movimiento pertenece a una caja específica.
    @ManyToOne // Un MovimientoEfectivo pertenece a una Caja
    @JoinColumn(name = "caja_id", nullable = false) // Columna caja_id en la tabla movimientos_efectivo
    @JsonIgnore
    private Caja caja;

    // Tipo de movimiento (por ejemplo: INGRESO, EGRESO).
    // El diagrama indica 'tipo ENUM', así que crearemos un enum para esto.
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoMovimiento tipo;

    public enum TipoMovimiento {
        INGRESO,
        EGRESO,
        // Agrega otros tipos si son necesarios, por ejemplo, APERTURA, CIERRE
    }

    @Column(name = "monto", precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    // Relación con Usuario: indica qué usuario realizó este movimiento.
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(name = "es_manual", nullable = false)
    private Boolean esManual = true; // Por defecto, true para los movimientos hechos manualmente

    // --- Getters y Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Caja getCaja() {
        return caja;
    }

    public void setCaja(Caja caja) {
        this.caja = caja;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Boolean getEsManual() {
        return esManual;
    }

    public void setEsManual(Boolean esManual) {
        this.esManual = esManual;
    }
}