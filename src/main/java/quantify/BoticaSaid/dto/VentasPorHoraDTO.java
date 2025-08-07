package quantify.BoticaSaid.dto;

public class VentasPorHoraDTO {
    private String hora;
    private double total;

    public VentasPorHoraDTO() {}

    public VentasPorHoraDTO(String hora, double total) {
        this.hora = hora;
        this.total = total;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}