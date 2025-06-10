package quantify.BoticaSaid.dto;

import java.math.BigDecimal;

public class CierreCajaDTO {
    private String dniUsuario;
    private BigDecimal efectivoFinalDeclarado;

    public String getDniUsuario() {
        return dniUsuario;
    }

    public void setDniUsuario(String dniUsuario) {
        this.dniUsuario = dniUsuario;
    }

    public BigDecimal getEfectivoFinalDeclarado() {
        return efectivoFinalDeclarado;
    }

    public void setEfectivoFinalDeclarado(BigDecimal efectivoFinalDeclarado) {
        this.efectivoFinalDeclarado = efectivoFinalDeclarado;
    }
}