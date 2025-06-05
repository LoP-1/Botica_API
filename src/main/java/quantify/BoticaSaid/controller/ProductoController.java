package quantify.BoticaSaid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import quantify.BoticaSaid.dto.ProductoRequest;
import quantify.BoticaSaid.model.Producto;
import quantify.BoticaSaid.service.ProductoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.AgregarStockRequest;

import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    // 1. Crear producto con su stock inicial
    @PostMapping("/nuevo")
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoRequest request) {
        Producto creado = productoService.crearProductoConStock(request);
        return ResponseEntity.status(201).body(creado);
    }

    // 2. Buscar producto por código de barras (y sus stocks)
    @GetMapping("/codigo-barras/{codigo}")
    public ResponseEntity<Producto> obtenerPorCodigoBarras(@PathVariable String codigo) {
        Producto producto = productoService.buscarPorCodigoBarras(codigo);
        return (producto != null)
                ? ResponseEntity.ok(producto)
                : ResponseEntity.notFound().build();
    }

    // 3. Listar todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
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

    @DeleteMapping("/{codigoBarras}")
    public ResponseEntity<?> eliminarPorCodigoBarras(@PathVariable String codigoBarras) {
        boolean eliminado = productoService.eliminarPorCodigoBarras(codigoBarras);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 7. Actualizar datos del producto
    @PutMapping("/{codigoBarras}")
    public ResponseEntity<Producto> actualizarPorCodigoBarras(
            @PathVariable String codigoBarras,
            @RequestBody ProductoRequest request) {
        Producto actualizado = productoService.actualizarPorCodigoBarras(codigoBarras, request);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    // 8. Ver productos con stock menor a cierto umbral
    @GetMapping("/stock-bajo")
    public ResponseEntity<List<Producto>> productosConStockBajo(
            @RequestParam(defaultValue = "10") int umbral) {
        return ResponseEntity.ok(productoService.buscarProductosConStockMenorA(umbral));
    }





}





