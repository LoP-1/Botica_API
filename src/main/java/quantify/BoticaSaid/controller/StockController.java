package quantify.BoticaSaid.controller;

import quantify.BoticaSaid.dto.StockItemDTO;
import quantify.BoticaSaid.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    // GET /api/stock
    @GetMapping
    public List<StockItemDTO> listarStock() {
        return stockService.listarStock();
    }

    // PUT /api/stock/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Void> actualizarStock(@PathVariable int id, @RequestBody StockItemDTO dto) {
        stockService.actualizarStock(id, dto);
        return ResponseEntity.ok().build();
    }
}