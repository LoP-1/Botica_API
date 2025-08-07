package quantify.BoticaSaid.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.CajaAperturaDTO;
import quantify.BoticaSaid.dto.CajaResumenDTO;
import quantify.BoticaSaid.dto.CierreCajaDTO;
import quantify.BoticaSaid.dto.MovimientoEfectivoDTO;
import quantify.BoticaSaid.model.Caja;
import quantify.BoticaSaid.service.CajaService;

import java.util.List;

@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final CajaService cajaService;

    public CajaController(CajaService cajaService) {
        this.cajaService = cajaService;
    }

    // Endpoint para abrir una caja - DEVOLVER DTO RESUMEN
    @PostMapping("/abrir")
    public ResponseEntity<CajaResumenDTO> abrirCaja(@RequestBody CajaAperturaDTO cajaAperturaDTO) {
        Caja nuevaCaja = cajaService.abrirCaja(cajaAperturaDTO);
        CajaResumenDTO dto = cajaService.obtenerResumenCajaActual(nuevaCaja.getUsuario().getDni());
        return ResponseEntity.ok(dto);
    }

    // Endpoint para cerrar una caja - DEVOLVER DTO RESUMEN
    @PostMapping("/cerrar")
    public ResponseEntity<CajaResumenDTO> cerrarCaja(@RequestBody CierreCajaDTO cierreCajaDTO) {
        Caja cajaCerrada = cajaService.cerrarCaja(cierreCajaDTO);
        CajaResumenDTO dto = cajaService.convertirCajaAResumen(cajaCerrada); // <-- usa el propio resumen de la caja cerrada
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/actual")
    public ResponseEntity<CajaResumenDTO> obtenerCajaActual(@RequestParam String dniUsuario) {
        CajaResumenDTO cajaResumen = cajaService.obtenerResumenCajaActual(dniUsuario);
        if (cajaResumen == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cajaResumen);
    }

    @GetMapping("/historial")
    public ResponseEntity<List<CajaResumenDTO>> obtenerHistorialCajas() {
        List<CajaResumenDTO> historial = cajaService.obtenerHistorialCajas();
        return ResponseEntity.ok(historial);
    }

    @GetMapping("/abiertas")
    public ResponseEntity<List<CajaResumenDTO>> obtenerCajasAbiertas() {
        List<CajaResumenDTO> abiertas = cajaService.obtenerCajasAbiertas();
        return ResponseEntity.ok(abiertas);
    }

    @PostMapping("/movimiento")
    public ResponseEntity<String> registrarMovimientoManual(@RequestBody MovimientoEfectivoDTO dto) {
        try {
            cajaService.registrarMovimientoManual(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Movimiento manual registrado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}