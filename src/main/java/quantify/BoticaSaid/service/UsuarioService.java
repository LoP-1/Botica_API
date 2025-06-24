package quantify.BoticaSaid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quantify.BoticaSaid.model.Usuario;
import quantify.BoticaSaid.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario actualizarUsuario(Long id, Usuario usuarioActualizado) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuarioExistente = optionalUsuario.get();
            usuarioExistente.setNombreCompleto(usuarioActualizado.getNombreCompleto());
            usuarioExistente.setDni(usuarioActualizado.getDni());
            usuarioExistente.setRol(usuarioActualizado.getRol());
            usuarioExistente.setTurno(usuarioActualizado.getTurno());
            usuarioExistente.setHorarioEntrada(usuarioActualizado.getHorarioEntrada());
            usuarioExistente.setHorarioSalida(usuarioActualizado.getHorarioSalida());
            // Otros campos...
            return usuarioRepository.save(usuarioExistente);
        } else {
            return null;
        }
    }

    public boolean eliminarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Usuario cambiarContrasena(Long id, String nuevaContrasena) {
        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);
        if (optionalUsuario.isPresent()) {
            Usuario usuario = optionalUsuario.get();
            usuario.setContrasena(nuevaContrasena);
            return usuarioRepository.save(usuario);
        } else {
            return null;
        }
    }
    public Usuario obtenerPorDni(String dni) {
        return usuarioRepository.findByDni(dni).orElse(null);
    }
}