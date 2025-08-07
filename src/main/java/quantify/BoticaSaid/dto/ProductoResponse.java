package quantify.BoticaSaid.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductoResponse {
    private String codigoBarras;
    private String nombre;
    private String concentracion;
    private int cantidadGeneral;
    private Integer cantidadMinima;
    private BigDecimal precioVentaUnd;
    private BigDecimal descuento;
    private String laboratorio;
    private String categoria;
    private Integer cantidadUnidadesBlister;
    private BigDecimal precioVentaBlister;
    private List<StockLoteDTO> stocks;


    // Getters y setters

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getConcentracion() {
        return concentracion;
    }

    public void setConcentracion(String concentracion) {
        this.concentracion = concentracion;
    }

    public int getCantidadGeneral() {
        return cantidadGeneral;
    }

    public void setCantidadGeneral(int cantidadGeneral) {
        this.cantidadGeneral = cantidadGeneral;
    }

    public Integer getCantidadMinima() {
        return cantidadMinima;
    }

    public void setCantidadMinima(Integer cantidadMinima) {
        this.cantidadMinima = cantidadMinima;
    }

    public BigDecimal getPrecioVentaUnd() {
        return precioVentaUnd;
    }

    public void setPrecioVentaUnd(BigDecimal precioVentaUnd) {
        this.precioVentaUnd = precioVentaUnd;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public String getLaboratorio() {
        return laboratorio;
    }

    public void setLaboratorio(String laboratorio) {
        this.laboratorio = laboratorio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    public Integer getCantidadUnidadesBlister() {
        return cantidadUnidadesBlister;
    }

    public void setCantidadUnidadesBlister(Integer cantidadUnidadesBlister) {
        this.cantidadUnidadesBlister = cantidadUnidadesBlister;
    }

    public BigDecimal getPrecioVentaBlister() {
        return precioVentaBlister;
    }

    public void setPrecioVentaBlister(BigDecimal precioVentaBlister) {
        this.precioVentaBlister = precioVentaBlister;
    }
    public List<StockLoteDTO> getStocks() { return stocks; }
    public void setStocks(List<StockLoteDTO> stocks) { this.stocks = stocks; }
}