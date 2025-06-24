package quantify.BoticaSaid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.model.Usuario;
import quantify.BoticaSaid.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. Listar todos los usuarios
    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioService.listarTodos();
    }

    // 2. Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 2b. Obtener usuario por DNI
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Usuario> obtenerUsuarioPorDni(@PathVariable String dni) {
        Usuario usuario = usuarioService.obtenerPorDni(dni);
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 3. Editar usuario por ID
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> editarUsuario(
            @PathVariable Long id,
            @RequestBody Usuario usuarioActualizado
    ) {
        Usuario actualizado = usuarioService.actualizarUsuario(id, usuarioActualizado);
        if (actualizado != null) {
            return ResponseEntity.ok(actualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Eliminar usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Long id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 5. Cambiar contraseña por ID
    @PatchMapping("/{id}/cambiar-contrasena")
    public ResponseEntity<Usuario> cambiarContrasena(
            @PathVariable Long id,
            @RequestBody CambiarContrasenaRequest request
    ) {
        Usuario usuario = usuarioService.cambiarContrasena(id, request.getContrasena());
        if (usuario != null) {
            return ResponseEntity.ok(usuario);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public static class CambiarContrasenaRequest {
        private String contrasena;
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }

}