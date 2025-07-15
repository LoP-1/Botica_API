package quantify.BoticaSaid.service;

import org.springframework.beans.factory.annotation.Autowired;
import quantify.BoticaSaid.dto.ProductoRequest;
import quantify.BoticaSaid.dto.ProductoResponse;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.model.Stock;
import quantify.BoticaSaid.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.HashMap;
import java.util.List;
import quantify.BoticaSaid.dto.AgregarStockRequest;
import quantify.BoticaSaid.repository.StockRepository;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private StockRepository stockRepository;

    // 1. Crear producto con stock (valida duplicidad por código de barras, permite reactivar)
    @Transactional
    public Object crearProductoConStock(ProductoRequest request) {

        int acumuladorPadre = 0;

        System.out.println("=== CREANDO PRODUCTO ===");
        System.out.println("Código de barras: " + request.getCodigoBarras());
        System.out.println("Stocks recibidos: " + (request.getStocks() != null ? request.getStocks().size() : 0));

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
                existente.setCantidadUnidadesBlister(request.getCantidadUnidadesBlister());
                existente.setPrecioVentaBlister(request.getPrecioVentaBlister());


                // ✅ CORREGIDO: Usar clear() y add() en lugar de setStocks()
                existente.getStocks().clear(); // Esto elimina automáticamente por orphanRemoval

                // Agregar nuevos stocks
                if (request.getStocks() != null && !request.getStocks().isEmpty()) {
                    for (var stockReq : request.getStocks()) {
                        Stock stock = new Stock();
                        stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                        stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                        stock.setPrecioCompra(stockReq.getPrecioCompra());
                        stock.setProducto(existente);
                        existente.getStocks().add(stock); // ✅ Usar add() en lugar de setStocks()
                        System.out.println("Stock agregado: " + stockReq.getCantidadUnidades() + " unidades, vence: " + stockReq.getFechaVencimiento());
                    }
                }

                Producto guardado = productoRepository.save(existente);
                System.out.println("Producto reactivado con " + guardado.getStocks().size() + " stocks");

                // Devuelve info especial para el frontend
                Map<String, Object> response = new HashMap<>();
                response.put("reactivado", true);
                response.put("producto", guardado);
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
        System.out.println("Hay " + request.getCantidadUnidadesBlister() + " unidades blister");
        producto.setCantidadUnidadesBlister(request.getCantidadUnidadesBlister());
        System.out.println("Ayuda huevon vole");
        producto.setPrecioVentaBlister(request.getPrecioVentaBlister());
        producto.setActivo(true);

        if (request.getStocks() != null && !request.getStocks().isEmpty()) {
            for (var stockReq : request.getStocks()) {
                Stock stock = new Stock();
                stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                acumuladorPadre +=  stockReq.getCantidadUnidades();
                stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                stock.setPrecioCompra(stockReq.getPrecioCompra());
                stock.setProducto(producto);
                producto.getStocks().add(stock); // ✅ Usar add() en lugar de setStocks()
                System.out.println("Nuevo stock: " + stockReq.getCantidadUnidades() + " unidades, vence: " + stockReq.getFechaVencimiento());
            }
        }

        producto.setCantidadGeneral(acumuladorPadre);
        Producto guardado = productoRepository.save(producto);
        System.out.println("Producto creado con " + guardado.getStocks().size() + " stocks");
        return guardado;
    }

    // 2. ✅ CORREGIDO: Buscar producto por código de barras con stocks
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        Optional<Producto> prodOpt = productoRepository.findByCodigoBarrasWithStocks(codigoBarras);
        if (prodOpt.isPresent() && prodOpt.get().isActivo()) {
            Producto prod = prodOpt.get();
            System.out.println("🔍 Producto encontrado: " + prod.getCodigoBarras() + " con " +
                    (prod.getStocks() != null ? prod.getStocks().size() : 0) + " stocks");
            return prod;
        }

        // Fallback a la consulta normal si no existe la consulta con stocks
        Producto prod = productoRepository.findByCodigoBarras(codigoBarras);
        return (prod != null && prod.isActivo()) ? prod : null;
    }

    // 3. ✅ CORREGIDO: Listar todos los productos activos con stocks
    public List<Producto> listarTodos() {
        try {
            List<Producto> productos = productoRepository.findByActivoTrueWithStocks();
            System.out.println("🔍 ProductoService.listarTodos() - Productos encontrados: " + productos.size());

            // Log detallado de cada producto y sus stocks
            for (Producto p : productos) {
                System.out.println("📦 Producto: " + p.getCodigoBarras() + " (" + p.getNombre() + ")");
                if (p.getStocks() != null && !p.getStocks().isEmpty()) {
                    System.out.println("   📦 Stocks: " + p.getStocks().size());
                    for (int i = 0; i < p.getStocks().size(); i++) {
                        Stock s = p.getStocks().get(i);
                        System.out.println("     - Stock " + i + ": " + s.getCantidadUnidades() + " unidades, vence: " + s.getFechaVencimiento());
                    }
                } else {
                    System.out.println("   ❌ Sin stocks");
                }
            }

            return productos;
        } catch (Exception e) {
            // Fallback a la consulta normal si falla la consulta con stocks
            System.out.println("⚠️ Error con consulta con stocks, usando fallback: " + e.getMessage());
            return productoRepository.findByActivoTrue();
        }
    }

    // 4. ✅ CORREGIDO: Agregar stock adicional
    @Transactional
    public boolean agregarStock(AgregarStockRequest request) {
        Optional<Producto> prodOpt = productoRepository.findByCodigoBarrasWithStocks(request.getCodigoBarras());
        Producto producto = null;

        if (prodOpt.isPresent()) {
            producto = prodOpt.get();
        } else {
            // Fallback
            producto = productoRepository.findByCodigoBarras(request.getCodigoBarras());
        }

        if (producto == null || !producto.isActivo()) {
            return false;
        }

        Stock nuevoStock = new Stock();
        nuevoStock.setCantidadUnidades(request.getCantidadUnidades());
        nuevoStock.setFechaVencimiento(request.getFechaVencimiento());
        nuevoStock.setPrecioCompra(request.getPrecioCompra());
        nuevoStock.setProducto(producto);

        // ✅ MEJOR: Usar add() para mantener la relación bidireccional
        producto.getStocks().add(nuevoStock);

        producto.setCantidadGeneral(producto.getCantidadGeneral() + request.getCantidadUnidades());
        productoRepository.save(producto);

        return true;
    }

    // 5. ✅ CORREGIDO: Buscar por nombre o categoría con stocks
    public List<Producto> buscarPorNombreOCategoria(String nombre, String categoria) {
        try {
            // Primero obtener todos los productos con stocks
            List<Producto> todosConStocks = productoRepository.findByActivoTrueWithStocks();

            System.out.println("🔍 Buscando en " + todosConStocks.size() + " productos con stocks cargados");

            if (nombre != null && categoria != null) {
                return todosConStocks.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase())
                                && p.getCategoria().toLowerCase().contains(categoria.toLowerCase()))
                        .toList();
            } else if (nombre != null) {
                return todosConStocks.stream()
                        .filter(p -> p.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                        .toList();
            } else if (categoria != null) {
                return todosConStocks.stream()
                        .filter(p -> p.getCategoria().toLowerCase().contains(categoria.toLowerCase()))
                        .toList();
            } else {
                return todosConStocks;
            }
        } catch (Exception e) {
            // Fallback a las consultas originales
            System.out.println("⚠️ Error con búsqueda con stocks, usando fallback: " + e.getMessage());
            if (nombre != null && categoria != null) {
                return productoRepository.findByNombreContainingIgnoreCaseAndCategoriaContainingIgnoreCaseAndActivoTrue(nombre, categoria);
            } else if (nombre != null) {
                return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
            } else if (categoria != null) {
                return productoRepository.findByCategoriaContainingIgnoreCaseAndActivoTrue(categoria);
            } else {
                return productoRepository.findByActivoTrue();
            }
        }
    }

    // 6. Borrado lógico (set activo=false)
    @Transactional
    public boolean eliminarPorCodigoBarras(String codigoBarras) {
        Producto producto = productoRepository.findByCodigoBarras(codigoBarras);
        if (producto != null && producto.isActivo()) {
            producto.setActivo(false);
            productoRepository.save(producto);
            return true;
        }
        return false;
    }

    // 7. ✅ CORREGIDO: Actualizar datos de un producto con stocks
    @Transactional
    public Producto actualizarPorCodigoBarras(String codigoBarras, ProductoRequest request) {
        System.out.println("=== ACTUALIZANDO PRODUCTO ===");
        System.out.println("Código de barras: " + codigoBarras);
        System.out.println("Stocks recibidos: " + (request.getStocks() != null ? request.getStocks().size() : 0));

        // ✅ Intentar obtener producto con stocks primero
        Optional<Producto> prodOpt = productoRepository.findByCodigoBarrasWithStocks(codigoBarras);
        Producto producto = null;

        if (prodOpt.isPresent()) {
            producto = prodOpt.get();
            System.out.println("✅ Producto obtenido con stocks: " + producto.getStocks().size());
        } else {
            // Fallback
            producto = productoRepository.findByCodigoBarras(codigoBarras);
            System.out.println("⚠️ Producto obtenido sin stocks (fallback)");
        }

        if (producto != null && producto.isActivo()) {
            producto.setNombre(request.getNombre());
            producto.setConcentracion(request.getConcentracion());
            producto.setCantidadGeneral(request.getCantidadGeneral());
            producto.setPrecioVentaUnd(request.getPrecioVentaUnd());
            producto.setDescuento(request.getDescuento());
            producto.setLaboratorio(request.getLaboratorio());
            producto.setCategoria(request.getCategoria());
            producto.setCantidadUnidadesBlister(request.getCantidadUnidadesBlister());
            producto.setPrecioVentaBlister(request.getPrecioVentaBlister());

            // ✅ CORREGIDO: Usar clear() en lugar de deleteAll() + setStocks()
            System.out.println("Eliminando " + producto.getStocks().size() + " stocks existentes");
            producto.getStocks().clear(); // Esto elimina automáticamente por orphanRemoval

            // Agregar nuevos stocks
            if (request.getStocks() != null && !request.getStocks().isEmpty()) {
                for (var stockReq : request.getStocks()) {
                    Stock stock = new Stock();
                    stock.setCantidadUnidades(stockReq.getCantidadUnidades());
                    stock.setFechaVencimiento(stockReq.getFechaVencimiento());
                    stock.setPrecioCompra(stockReq.getPrecioCompra());
                    stock.setProducto(producto);
                    producto.getStocks().add(stock); // ✅ Usar add() en lugar de setStocks()
                    System.out.println("Nuevo stock en actualización: " + stockReq.getCantidadUnidades() + " unidades, vence: " + stockReq.getFechaVencimiento());
                }
            }

            Producto guardado = productoRepository.save(producto);
            System.out.println("Producto actualizado con " + guardado.getStocks().size() + " stocks");

            // ✅ VERIFICAR QUE LOS STOCKS SE GUARDARON
            Optional<Producto> verificacion = productoRepository.findByCodigoBarrasWithStocks(codigoBarras);
            if (verificacion.isPresent()) {
                System.out.println("✅ Verificación: Producto tiene " + verificacion.get().getStocks().size() + " stocks después de actualizar");
                return verificacion.get(); // Devolver el producto con stocks frescos
            } else {
                return guardado;
            }
        }
        return null;
    }

    // 8. ✅ CORREGIDO: Buscar productos con stock menor a cierto umbral con stocks
    public List<Producto> buscarProductosConStockMenorA(int umbral) {
        try {
            List<Producto> productos = productoRepository.findByActivoTrueWithStocks();
            List<Producto> resultado = new ArrayList<>();
            for (Producto p : productos) {
                if (p.getCantidadGeneral() < umbral) {
                    resultado.add(p);
                }
            }
            return resultado;
        } catch (Exception e) {
            // Fallback
            List<Producto> productos = productoRepository.findByActivoTrue();
            List<Producto> resultado = new ArrayList<>();
            for (Producto p : productos) {
                if (p.getCantidadGeneral() < umbral) {
                    resultado.add(p);
                }
            }
            return resultado;
        }
    }

    public ProductoResponse toProductoResponse(Producto producto) {
        ProductoResponse resp = new ProductoResponse();
        resp.setCodigoBarras(producto.getCodigoBarras());
        resp.setNombre(producto.getNombre());
        resp.setConcentracion(producto.getConcentracion());
        resp.setCantidadGeneral(producto.getCantidadGeneral());
        resp.setPrecioVentaUnd(producto.getPrecioVentaUnd());
        resp.setDescuento(producto.getDescuento());
        resp.setLaboratorio(producto.getLaboratorio());
        resp.setCategoria(producto.getCategoria());
        return resp;
    }
}