package quantify.BoticaSaid.service;

import org.springframework.beans.factory.annotation.Autowired;
import quantify.BoticaSaid.dto.ProductoRequest;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import quantify.BoticaSaid.dto.AgregarStockRequest;
import quantify.BoticaSaid.repository.StockRepository;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private StockRepository stockRepository;

    // 1. Crear producto con stock
    public Producto crearProductoConStock(ProductoRequest request) {
        Producto producto = new Producto();
        producto.setCodigoBarras(request.getCodigoBarras());
        producto.setNombre(request.getNombre());
        producto.setConcentracion(request.getConcentracion());
        producto.setCantidadGeneral(request.getCantidadGeneral());
        producto.setPrecioVentaUnd(request.getPrecioVentaUnd());
        producto.setDescuento(request.getDescuento());
        producto.setLaboratorio(request.getLaboratorio());
        producto.setCategoria(request.getCategoria());

        // Mapear stocks si vienen en la solicitud
        if (request.getStocks() != null && !request.getStocks().isEmpty()) {
            List<Stock> stocks = request.getStocks().stream().map(stockReq -> {
                Stock stock = new Stock();
                stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                stock.setPrecioCompra(stockReq.getPrecioCompra());
                stock.setProducto(producto);
                return stock;
            }).toList();
            producto.setStocks(stocks);
        } else {
            producto.setStocks(new ArrayList<>());
        }

        return productoRepository.save(producto);
    }

    // 2. Buscar producto por código de barras
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras);
    }

    // 3. Listar todos los productos
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    // 4. Agregar stock adicional
    public boolean agregarStock(AgregarStockRequest request) {
        Producto producto = productoRepository.findByCodigoBarras(request.getCodigoBarras());
        if (producto == null) {
            return false;
        }

        Stock nuevoStock = new Stock();
        nuevoStock.setCantidadUnidades(request.getCantidadUnidades());
        nuevoStock.setFechaVencimiento(request.getFechaVencimiento());
        nuevoStock.setPrecioCompra(request.getPrecioCompra());
        nuevoStock.setProducto(producto);

        stockRepository.save(nuevoStock);

        producto.setCantidadGeneral(producto.getCantidadGeneral() + request.getCantidadUnidades());
        productoRepository.save(producto);

        return true;
    }

    // 5. Buscar por nombre o categoría
    public List<Producto> buscarPorNombreOCategoria(String nombre, String categoria) {
        if (nombre != null && categoria != null) {
            return productoRepository.findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCase(nombre, categoria);
        } else if (nombre != null) {
            return productoRepository.findByNombreContainingIgnoreCase(nombre);
        } else if (categoria != null) {
            return productoRepository.findByCategoriaContainingIgnoreCase(categoria);
        } else {
            return listarTodos();
        }
    }

    // 6. Eliminar producto por ID
    public boolean eliminarPorCodigoBarras(String codigoBarras) {
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras);
        if (producto != null) {
            productoRepository.delete(producto);
            return true;
        }
        return false;
    }


    // 7. Actualizar datos de un producto
    public Producto actualizarPorCodigoBarras(String codigoBarras, ProductoRequest request) {
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras);
        if (producto != null) {
            producto.setNombre(request.getNombre());
            producto.setConcentracion(request.getConcentracion());
            producto.setCantidadGeneral(request.getCantidadGeneral());
            producto.setPrecioVentaUnd(request.getPrecioVentaUnd());
            producto.setDescuento(request.getDescuento());
            producto.setLaboratorio(request.getLaboratorio());
            producto.setCategoria(request.getCategoria());
            // No cambiar codigoBarras para evitar conflicto
            return productoRepository.save(producto);
        }
        return null;
    }


    // 8. Buscar productos con stock menor a cierto umbral
    public List<Producto> buscarProductosConStockMenorA(int umbral) {
        List<Producto> productos = productoRepository.findAll();
        List<Producto> resultado = new ArrayList<>();

        for (Producto p : productos) {
            if (p.getCantidadGeneral() < umbral) {
                resultado.add(p);
            }
        }

        return resultado;
    }
}
