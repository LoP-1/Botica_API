package quantify.BoticaSaid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import quantify.BoticaSaid.dto.ProductoRequest;
import quantify.BoticaSaid.dto.ProductoResponse;
import quantify.BoticaSaid.dto.AgregarStockRequest;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.service.ProductoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 1. Crear producto con su stock inicial
    @PostMapping("/nuevo")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request) {
        try {
            Object result = productoService.crearProductoConStock(request);
            if (result instanceof Map) {
                return ResponseEntity.ok(result); // 200 OK si fue reactivado
            } else if (result instanceof Producto) {
                return ResponseEntity.status(201).body(result); // 201 Created si es nuevo
            } else {
                return ResponseEntity.status(500).body("Error inesperado.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    // 2. Buscar producto por código de barras (y sus stocks)
    @GetMapping("/codigo-barras/{codigo}")
    public ResponseEntity<Producto> obtenerPorCodigoBarras(@PathVariable String codigo) {
        Producto producto = productoService.buscarPorCodigoBarras(codigo);
        return (producto != null)
                ? ResponseEntity.ok(producto)
                : ResponseEntity.notFound().build();
    }

    // 3. ✅ CORREGIDO: GET /productos devuelve Producto completo con stocks
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        List<Producto> productos = productoService.listarTodos();

        // ✅ Log para debugging
        System.out.println("🔍 GET /productos - Devolviendo " + productos.size() + " productos");
        for (Producto p : productos) {
            System.out.println("📦 Producto: " + p.getCodigoBarras() + " con " +
                    (p.getStocks() != null ? p.getStocks().size() : 0) + " stocks");
        }

        return ResponseEntity.ok(productos); // ✅ Devuelve Producto completo, no ProductoResponse
    }

    // 4. Agregar stock adicional a un producto existente
    @PostMapping("/agregar-stock")
    public ResponseEntity<?> agregarStock(@RequestBody AgregarStockRequest request) {
        boolean exito = productoService.agregarStock(request);
        return exito
                ? ResponseEntity.ok("Stock agregado correctamente.")
                : ResponseEntity.badRequest().body("Producto no encontrado.");
    }

    // 5. Buscar por nombre o categoría
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombreOCategoria(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {
        return ResponseEntity.ok(productoService.buscarPorNombreOCategoria(nombre, categoria));
    }

    // 6. Eliminar producto (borrado lógico)
    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<?> eliminarPorCodigoBarras(@PathVariable String codigoBarras) {
        boolean eliminado = productoService.eliminarPorCodigoBarras(codigoBarras);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 7. ✅ CORREGIDO: Actualizar datos del producto con logs
    @PutMapping("/{codigoBarras}")
    public ResponseEntity<Producto> actualizarPorCodigoBarras(
            @PathVariable String codigoBarras,
            @RequestBody ProductoRequest request) {

        System.out.println("🔄 PUT /productos/" + codigoBarras + " - Actualizando producto");
        System.out.println("📦 Stocks recibidos en request: " +
                (request.getStocks() != null ? request.getStocks().size() : 0));

        Producto actualizado = productoService.actualizarPorCodigoBarras(codigoBarras, request);

        if (actualizado != null) {
            System.out.println("✅ Producto actualizado exitosamente");
            System.out.println("📦 Stocks en producto actualizado: " +
                    (actualizado.getStocks() != null ? actualizado.getStocks().size() : 0));
            return ResponseEntity.ok(actualizado);
        } else {
            System.out.println("❌ Producto no encontrado para actualizar");
            return ResponseEntity.notFound().build();
        }
    }

    // 8. Ver productos con stock menor a cierto umbral
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> productosConStockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(productoService.buscarProductosConStockMenorA(umbral));
    }

    // ✅ OPCIONAL: Endpoint separado para ProductoResponse si necesitas uno ligero
    @GetMapping("/ligero")
    public ResponseEntity<List<ProductoResponse>> listarTodosLigero() {
        List<Producto> productos = productoService.listarTodos();
        List<ProductoResponse> productosRes = productos.stream()
                .map(this::toProductoResponse)
                .toList();
        return ResponseEntity.ok(productosRes);
    }

    // ✅ CORREGIDO: Método helper para ProductoResponse (sin stocks para respuestas ligeras)
    private ProductoResponse toProductoResponse(Producto producto) {
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