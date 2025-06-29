package quantify.BoticaSaid.service;

import org.springframework.beans.factory.annotation.Autowired;
import quantify.BoticaSaid.dto.ProductoRequest;
import quantify.BoticaSaid.dto.ProductoResponse;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import quantify.BoticaSaid.dto.AgregarStockRequest;
import quantify.BoticaSaid.repository.StockRepository;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private StockRepository stockRepository;

    // 1. Crear producto con stock (valida duplicidad por código de barras, permite reactivar)
    public Object crearProductoConStock(ProductoRequest request) {
        Producto existente = productoRepository.findByCodigoBarras(request.getCodigoBarras());
        if (existente != null) {
            if (!existente.isActivo()) {
                // Reactivar y actualizar datos
                existente.setActivo(true);
                existente.setNombre(request.getNombre());
                existente.setConcentracion(request.getConcentracion());
                existente.setCantidadGeneral(request.getCantidadGeneral());
                existente.setPrecioVentaUnd(request.getPrecioVentaUnd());
                existente.setDescuento(request.getDescuento());
                existente.setLaboratorio(request.getLaboratorio());
                existente.setCategoria(request.getCategoria());
                // Actualiza stocks
                if (request.getStocks() != null) {
                    // Inicializa la lista si es null
                    if (existente.getStocks() == null) {
                        existente.setStocks(new ArrayList<>());
                    }
                    // Limpia la lista actual (elimina los stocks existentes)
                    existente.getStocks().clear();
                    // Agrega los nuevos stocks
                    for (var stockReq : request.getStocks()) {
                        Stock stock = new Stock();
                        stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                        stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                        stock.setPrecioCompra(stockReq.getPrecioCompra());
                        stock.setProducto(existente);
                        existente.getStocks().add(stock);
                    }
                } else {
                    // Si no hay stocks nuevos, limpia la lista existente
                    if (existente.getStocks() != null) {
                        existente.getStocks().clear();
                    }
                }
                productoRepository.save(existente);

                // Devuelve info especial para el frontend
                Map<String, Object> response = new HashMap<>();
                response.put("reactivado", true);
                response.put("producto", existente);
                return response;
            } else {
                throw new IllegalArgumentException("Ya existe un producto activo con ese código de barras.");
            }
        }

        // Crear producto normalmente
        Producto producto = new Producto();
        producto.setCodigoBarras(request.getCodigoBarras());
        producto.setNombre(request.getNombre());
        producto.setConcentracion(request.getConcentracion());
        producto.setCantidadGeneral(request.getCantidadGeneral());
        producto.setPrecioVentaUnd(request.getPrecioVentaUnd());
        producto.setDescuento(request.getDescuento());
        producto.setLaboratorio(request.getLaboratorio());
        producto.setCategoria(request.getCategoria());
        producto.setActivo(true);

        if (request.getStocks() != null && !request.getStocks().isEmpty()) {
            List<Stock> stocks = request.getStocks().stream().map(stockReq -> {
                Stock stock = new Stock();
                stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                stock.setPrecioCompra(stockReq.getPrecioCompra());
                stock.setProducto(producto);
                return stock;
            }).collect(Collectors.toList());
            producto.setStocks(stocks);
        } else {
            producto.setStocks(new ArrayList<>());
        }

        return productoRepository.save(producto);
    }

    // 2. Buscar producto por código de barras (solo activos)
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        Producto prod = productoRepository.findByCodigoBarras(codigoBarras);
        return (prod != null && prod.isActivo()) ? prod : null;
    }

    // 3. Listar todos los productos activos
    public List<Producto> listarTodos() {
        return productoRepository.findByActivoTrue();
    }

    // 4. Agregar stock adicional
    public boolean agregarStock(AgregarStockRequest request) {
        Producto producto = productoRepository.findByCodigoBarras(request.getCodigoBarras());
        if (producto == null || !producto.isActivo()) {
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

    // 5. Buscar por nombre o categoría (solo activos)
    public List<Producto> buscarPorNombreOCategoria(String nombre, String categoria) {
        if (nombre != null && categoria != null) {
            return productoRepository.findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCaseAndActivoTrue(nombre, categoria);
        } else if (nombre != null) {
            return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
        } else if (categoria != null) {
            return productoRepository.findByCategoriaContainingIgnoreCaseAndActivoTrue(categoria);
        } else {
            return listarTodos();
        }
    }

    // 6. Borrado lógico (set activo=false)
    public boolean eliminarPorCodigoBarras(String codigoBarras) {
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras);
        if (producto != null && producto.isActivo()) {
            producto.setActivo(false);
            productoRepository.save(producto);
            return true;
        }
        return false;
    }

    // 7. Actualizar datos de un producto (solo si activo)
    public Producto actualizarPorCodigoBarras(String codigoBarras, ProductoRequest request) {
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras);
        if (producto != null && producto.isActivo()) {
            producto.setNombre(request.getNombre());
            producto.setConcentracion(request.getConcentracion());
            producto.setCantidadGeneral(request.getCantidadGeneral());
            producto.setPrecioVentaUnd(request.getPrecioVentaUnd());
            producto.setDescuento(request.getDescuento());
            producto.setLaboratorio(request.getLaboratorio());
            producto.setCategoria(request.getCategoria());
            return productoRepository.save(producto);
        }
        return null;
    }

    // 8. Buscar productos con stock menor a cierto umbral (solo activos)
    public List<Producto> buscarProductosConStockMenorA(int umbral) {
        List<Producto> productos = productoRepository.findByActivoTrue();
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : productos) {
            if (p.getCantidadGeneral() < umbral) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public ProductoResponse toProductoResponse(Producto producto) {
        ProductoResponse resp = new ProductoResponse();
        resp.setCodigoBarras(producto.getCodigoBarras());
        resp.setNombre(producto.getNombre());
        resp.setConcentracion(producto.getConcentracion());
        resp.setCantidadGeneral(producto.getCantidadGeneral());
        resp.setPrecioVentaUnd(producto.getPrecioVentaUnd());
        resp.setDescuento(producto.getDescuento()); // ← PASA EL MONTO DIRECTO
        resp.setLaboratorio(producto.getLaboratorio());
        resp.setCategoria(producto.getCategoria());
        return resp;
    }
}