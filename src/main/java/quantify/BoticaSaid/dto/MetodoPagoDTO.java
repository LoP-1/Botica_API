package quantify.BoticaSaid.dto;

public class MetodoPagoDTO {

    private String nombre;
    private Double efectivo;
    private Double digital;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(Double efectivo) {
        this.efectivo = efectivo;
    }

    public Double getDigital() {
        return digital;
    }

    public void setDigital(Double digital) {
        this.digital = digital;
    }
}
