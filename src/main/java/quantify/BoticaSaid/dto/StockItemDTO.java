package quantify.BoticaSaid.dto;

import java.math.BigDecimal;

public class StockItemDTO {
    private int id;
    private String codigoStock;
    private String codigoBarras;
    private String nombre;
    private String concentracion;
    private int cantidadUnidades;
    private Integer cantidadMinima; // Puedes mapearlo a cantidadGeneral si aún no tienes campo específico
    private BigDecimal precioCompra;
    private BigDecimal precioVenta; // <-- añade este campo si lo necesitas en frontend
    private String fechaVencimiento; // <-- ahora es String, formato "yyyy-MM-dd"
    private String laboratorio;
    private String categoria;

    public StockItemDTO() {}

    public StockItemDTO(
            int id,
            String codigoStock,
            String codigoBarras,
            String nombre,
            String concentracion,
            int cantidadUnidades,
            Integer cantidadMinima,
            BigDecimal precioCompra,
            BigDecimal precioVenta,
            String fechaVencimiento,
            String laboratorio,
            String categoria
    ) {
        this.id = id;
        this.codigoStock = codigoStock;
        this.codigoBarras = codigoBarras;
        this.nombre = nombre;
        this.concentracion = concentracion;
        this.cantidadUnidades = cantidadUnidades;
        this.cantidadMinima = cantidadMinima;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.fechaVencimiento = fechaVencimiento;
        this.laboratorio = laboratorio;
        this.categoria = categoria;
    }

    // Getters y Setters


    public String getCodigoStock() {
        return codigoStock;
    }

    public void setCodigoStock(String codigoStock) {
        this.codigoStock = codigoStock;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigoBarras() { return codigoBarras; }
    public void setCodigoBarras(String codigoBarras) { this.codigoBarras = codigoBarras; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getConcentracion() { return concentracion; }
    public void setConcentracion(String concentracion) { this.concentracion = concentracion; }

    public int getCantidadUnidades() { return cantidadUnidades; }
    public void setCantidadUnidades(int cantidadUnidades) { this.cantidadUnidades = cantidadUnidades; }

    public int getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(int cantidadMinima) { this.cantidadMinima = cantidadMinima; }

    public BigDecimal getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(BigDecimal precioCompra) { this.precioCompra = precioCompra; }

    public BigDecimal getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(BigDecimal precioVenta) { this.precioVenta = precioVenta; }

    public String getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public String getLaboratorio() { return laboratorio; }
    public void setLaboratorio(String laboratorio) { this.laboratorio = laboratorio; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
}