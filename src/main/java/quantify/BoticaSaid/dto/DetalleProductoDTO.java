package quantify.BoticaSaid.dto;

public class DetalleProductoDTO {
    private String codBarras;
    private int cantidad;


    public String getCodBarras() {
        return codBarras;
    }

    public void setCodBarras(String codBarras) {
        this.codBarras = codBarras;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
