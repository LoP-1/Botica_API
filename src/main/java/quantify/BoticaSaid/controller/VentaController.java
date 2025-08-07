package quantify.BoticaSaid.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.ProductoResponse;
import quantify.BoticaSaid.dto.VentaRequestDTO;
import quantify.BoticaSaid.dto.VentaResponseDTO;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.service.VentaService;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<String> registrarVenta(@RequestBody VentaRequestDTO ventaDTO) {
        System.out.println("Numero recibido en Controller: " + ventaDTO.getNumero());
        try {
            ventaService.registrarVenta(ventaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Venta registrada exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error al registrar venta: " + e.getMessage());
        }
    }
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        List<VentaResponseDTO> ventas = ventaService.listarVentas();
        return ResponseEntity.ok(ventas);
    }



}

