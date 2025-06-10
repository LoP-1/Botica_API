package quantify.BoticaSaid.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.CajaAperturaDTO;
import quantify.BoticaSaid.dto.CierreCajaDTO;
import quantify.BoticaSaid.model.Caja;
import quantify.BoticaSaid.service.CajaService;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    // Endpoint para abrir una caja
    @PostMapping("/abrir")
    public ResponseEntity<Caja> abrirCaja(@RequestBody CajaAperturaDTO cajaAperturaDTO) {
        Caja nuevaCaja = cajaService.abrirCaja(cajaAperturaDTO);
        return ResponseEntity.ok(nuevaCaja);
    }

    @PostMapping("/cerrar")
    public ResponseEntity<Caja> cerrarCaja(@RequestBody CierreCajaDTO cierreCajaDTO) {
        Caja cajaCerrada = cajaService.cerrarCaja(cierreCajaDTO);
        return ResponseEntity.ok(cajaCerrada);
    }
}
