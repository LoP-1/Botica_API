package quantify.BoticaSaid.dto;

import java.math.BigDecimal;
import java.util.List;

public class CajaResumenDTO {
    private BigDecimal efectivoInicial;
    private BigDecimal efectivoFinal;
    private BigDecimal ingresos;
    private BigDecimal egresos;
    private BigDecimal ventasEfectivo;
    private BigDecimal ventasYape;
    private BigDecimal ventasPlin;
    private BigDecimal ventasMixto;
    private BigDecimal totalVentas;
    private BigDecimal efectivo; // Efectivo actual en caja (calculado)
    private List<MovimientoDTO> movimientos;
    private BigDecimal diferencia; // Faltante/sobrante al cierre (si aplica)
    private boolean cajaAbierta;
    private String fechaApertura;
    private String fechaCierre;
    private String usuarioResponsable;

    private BigDecimal totalYape; // <--- AGREGADO

    // Getters y Setters

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

    public BigDecimal getIngresos() {
        return ingresos;
    }

    public void setIngresos(BigDecimal ingresos) {
        this.ingresos = ingresos;
    }

    public BigDecimal getEgresos() {
        return egresos;
    }

    public void setEgresos(BigDecimal egresos) {
        this.egresos = egresos;
    }

    public BigDecimal getVentasEfectivo() {
        return ventasEfectivo;
    }

    public void setVentasEfectivo(BigDecimal ventasEfectivo) {
        this.ventasEfectivo = ventasEfectivo;
    }

    public BigDecimal getVentasYape() {
        return ventasYape;
    }

    public void setVentasYape(BigDecimal ventasYape) {
        this.ventasYape = ventasYape;
    }

    public BigDecimal getVentasPlin() {
        return ventasPlin;
    }

    public void setVentasPlin(BigDecimal ventasPlin) {
        this.ventasPlin = ventasPlin;
    }

    public BigDecimal getVentasMixto() {
        return ventasMixto;
    }

    public void setVentasMixto(BigDecimal ventasMixto) {
        this.ventasMixto = ventasMixto;
    }

    public BigDecimal getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(BigDecimal totalVentas) {
        this.totalVentas = totalVentas;
    }

    public BigDecimal getEfectivo() {
        return efectivo;
    }

    public void setEfectivo(BigDecimal efectivo) {
        this.efectivo = efectivo;
    }

    public List<MovimientoDTO> getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(List<MovimientoDTO> movimientos) {
        this.movimientos = movimientos;
    }

    public BigDecimal getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(BigDecimal diferencia) {
        this.diferencia = diferencia;
    }

    public boolean isCajaAbierta() {
        return cajaAbierta;
    }

    public void setCajaAbierta(boolean cajaAbierta) {
        this.cajaAbierta = cajaAbierta;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    public void setUsuarioResponsable(String usuarioResponsable) {
        this.usuarioResponsable = usuarioResponsable;
    }

    public BigDecimal getTotalYape() {
        return totalYape;
    }

    public void setTotalYape(BigDecimal totalYape) {
        this.totalYape = totalYape;
    }
}