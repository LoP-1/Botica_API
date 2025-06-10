package quantify.BoticaSaid.dto;

import java.math.BigDecimal;

public class CajaAperturaDTO {
    private String dniUsuario;
    private BigDecimal efectivoInicial;

    public String getDniUsuario() {
        return dniUsuario;
    }

    public void setDniUsuario(String dniUsuario) {
        this.dniUsuario = dniUsuario;
    }

    public BigDecimal getEfectivoInicial() {
        return efectivoInicial;
    }

    public void setEfectivoInicial(BigDecimal efectivoInicial) {
        this.efectivoInicial = efectivoInicial;
    }
}

