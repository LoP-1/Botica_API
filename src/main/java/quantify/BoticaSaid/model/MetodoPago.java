package quantify.BoticaSaid.model;

import jakarta.persistence.*;

@Entity
@Table(name = "metodo_pago")
public class MetodoPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private NombreMetodo nombre;

    public enum NombreMetodo {
        EFECTIVO, TARJETA, YAPE, PLIN , MIXTO
    }

    @Column(name = "efectivo")
    private double efectivo;

    @Column(name = "yape")
    private double digital;

    @OneToOne
    @JoinColumn(name = "boleta_id", referencedColumnName = "id")
    private Boleta boleta;

    // Getters y setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public NombreMetodo getNombre() {
        return nombre;
    }

    public void setNombre(NombreMetodo nombre) {
        this.nombre = nombre;
    }

    public double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(double efectivo) {
        this.efectivo = efectivo;
    }

    public double getDigital() {
        return digital;
    }

    public void setDigital(double digital) {
        this.digital = digital;
    }

    public Boleta getBoleta() {
        return boleta;
    }

    public void setBoleta(Boleta boleta) {
        this.boleta = boleta;
    }
}

