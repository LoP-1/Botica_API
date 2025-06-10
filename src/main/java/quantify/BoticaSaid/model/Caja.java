package quantify.BoticaSaid.model; // Asegúrate de que el paquete sea el correcto

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "caja")
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // El diagrama muestra INT(11)

    @ManyToOne // Una Caja pertenece a un Usuario
    @JoinColumn(name = "usuario_id", nullable = false) // Columna usuario_id en la tabla caja
    private Usuario usuario;

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre") // Puede ser null si la caja está abierta
    private LocalDateTime fechaCierre;

    @Column(name = "efectivo_inicial", precision = 12, scale = 2, nullable = false)
    private BigDecimal efectivoInicial;

    @Column(name = "efectivo_final", precision = 12, scale = 2) // Puede ser null hasta que se cierra
    private BigDecimal efectivoFinal;

    @Column(name = "total_yape", precision = 12, scale = 2) // Puede ser null si no hay transacciones yape
    private BigDecimal totalYape;

    @Column(name = "diferencia", precision = 12, scale = 2) // Puede ser null hasta que se cierra y se calcula
    private BigDecimal diferencia;

    // Relación inversa para que desde Caja puedas ver sus movimientos
    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<MovimientoEfectivo> movimientos;

    // --- Getters y Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(LocalDateTime fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public BigDecimal getEfectivoInicial() {
        return efectivoInicial;
    }

    public void setEfectivoInicial(BigDecimal efectivoInicial) {
        this.efectivoInicial = efectivoInicial;
    }

    public BigDecimal getEfectivoFinal() {
        return efectivoFinal;
    }

    public void setEfectivoFinal(BigDecimal efectivoFinal) {
        this.efectivoFinal = efectivoFinal;
    }

    public BigDecimal getTotalYape() {
        return totalYape;
    }

    public void setTotalYape(BigDecimal totalYape) {
        this.totalYape = totalYape;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public java.util.List<MovimientoEfectivo> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(java.util.List<MovimientoEfectivo> movimientos) {
        this.movimientos = movimientos;
    }
}