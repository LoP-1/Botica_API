package quantify.BoticaSaid.service;

import quantify.BoticaSaid.dto.StockItemDTO;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<StockItemDTO> listarStock() {
        List<Stock> stocks = stockRepository.findAllWithProducto();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        return stocks.stream().map(stock -> {
            Producto producto = stock.getProducto();
            String fechaIso = stock.getFechaVencimiento() != null
                    ? sdf.format(stock.getFechaVencimiento())
                    : null;
            return new StockItemDTO(
                    stock.getId(),
                    producto.getCodigoBarras(),
                    producto.getNombre(),
                    producto.getConcentracion(),
                    stock.getCantidadUnidades(),
                    producto.getCantidadGeneral(), // cantidadMinima
                    stock.getPrecioCompra(),
                    producto.getPrecioVentaUnd(), // <-- asegúrate que este getter existe
                    fechaIso,
                    producto.getLaboratorio(),
                    producto.getCategoria()
            );
        }).collect(Collectors.toList());
    }

    public void actualizarStock(int id, StockItemDTO dto) {
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock no encontrado"));
        stock.setCantidadUnidades(dto.getCantidadUnidades());
        stock.setPrecioCompra(dto.getPrecioCompra());
        // Convierte fecha string de vuelta a Date si es necesario
        try {
            if (dto.getFechaVencimiento() != null) {
                stock.setFechaVencimiento(new SimpleDateFormat("yyyy-MM-dd").parse(dto.getFechaVencimiento()));
            }
        } catch (Exception e) {
            // Manejar parseo
            stock.setFechaVencimiento(null);
        }
        stockRepository.save(stock);
    }
}