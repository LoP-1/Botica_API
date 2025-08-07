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
                Producto producto = (Producto) result;
                ProductoResponse resp = productoService.toProductoResponse(producto);
                return ResponseEntity.status(201).body(resp); // 201 Created si es nuevo
            } else {
                return ResponseEntity.status(500).body("Error inesperado.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(Map.of("message", e.getMessage()));
        }
    }

    // 2. Buscar producto por código de barras (y sus stocks)
    @GetMapping("/codigo-barras/{codigo}")
    public ResponseEntity<ProductoResponse> obtenerPorCodigoBarras(@PathVariable String codigo) {
        Producto producto = productoService.buscarPorCodigoBarras(codigo);
        if (producto != null) {
            ProductoResponse resp = productoService.toProductoResponse(producto);
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. GET /productos devuelve ProductoResponse completo con stocks
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        List<Producto> productos = productoService.listarTodos();
        List<ProductoResponse> productosRes = productos.stream()
                .map(productoService::toProductoResponse)
                .toList();

        // Log para debugging
        System.out.println("🔍 GET /productos - Devolviendo " + productosRes.size() + " productos");
        for (ProductoResponse p : productosRes) {
            System.out.println("📦 Producto: " + p.getCodigoBarras());
        }

        return ResponseEntity.ok(productosRes);
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
    public ResponseEntity<List<ProductoResponse>> buscarPorNombreOCategoria(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String categoria) {
        List<Producto> productos = productoService.buscarPorNombreOCategoria(nombre, categoria);
        List<ProductoResponse> productosRes = productos.stream()
                .map(productoService::toProductoResponse)
                .toList();
        return ResponseEntity.ok(productosRes);
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

    // 7. Actualizar datos del producto con logs
    @PutMapping("/{codigoBarras}")
    public ResponseEntity<ProductoResponse> actualizarPorCodigoBarras(
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
            ProductoResponse resp = productoService.toProductoResponse(actualizado);
            return ResponseEntity.ok(resp);
        } else {
            System.out.println("❌ Producto no encontrado para actualizar");
            return ResponseEntity.notFound().build();
        }
    }

    // 8. Ver productos con stock menor a cierto umbral
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> productosConStockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        List<Producto> productos = productoService.buscarProductosConStockMenorA(umbral);
        List<ProductoResponse> productosRes = productos.stream()
                .map(productoService::toProductoResponse)
                .toList();
        return ResponseEntity.ok(productosRes);
    }
}