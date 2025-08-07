package quantify.BoticaSaid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import quantify.BoticaSaid.dto.AuthRequest;
import quantify.BoticaSaid.dto.AuthResponse;
import quantify.BoticaSaid.dto.RegisterRequest;
import quantify.BoticaSaid.dto.UsuarioDto;
import quantify.BoticaSaid.enums.EstadoToken;
import quantify.BoticaSaid.enums.Rol;
import quantify.BoticaSaid.enums.TipoToken;
import quantify.BoticaSaid.jwt.JwtUtil;
import quantify.BoticaSaid.model.Token;
import quantify.BoticaSaid.model.Usuario;
import quantify.BoticaSaid.repository.TokenRepository;
import quantify.BoticaSaid.repository.UsuarioRepository;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenRepository tokenRepository;

    public AuthResponse login(AuthRequest request) {
        Usuario usuario = usuarioRepo.findByDni(request.getDni())
                .orElseThrow(() -> new UsernameNotFoundException("DNI no encontrado"));

        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        // Generar token JWT usando el DNI
        String jwt = jwtUtil.generarToken(usuario.getDni());

        // Invalidar tokens previos
        invalidarTokensPrevios(usuario);

        // Guardar el nuevo token
        Token token = new Token();
        token.setToken(jwt);
        token.setUsuario(usuario);
        token.setTipoToken(TipoToken.BEARER);
        token.setEstadoToken(EstadoToken.VALIDO);
        tokenRepository.save(token);

        // Construir el UsuarioDto (convertir LocalTime a String)
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        UsuarioDto usuarioDto = new UsuarioDto(
                usuario.getDni(),
                usuario.getNombreCompleto(),
                usuario.getRol().toString(),
                usuario.getHorarioEntrada() != null ? usuario.getHorarioEntrada().format(timeFormatter) : null,
                usuario.getHorarioSalida() != null ? usuario.getHorarioSalida().format(timeFormatter) : null,
                usuario.getTurno()
        );

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);
        response.setUsuario(usuarioDto);

        return response;
    }

    private void invalidarTokensPrevios(Usuario usuario) {
        var tokens = tokenRepository.findAllByUsuarioAndEstadoToken(usuario, EstadoToken.VALIDO);
        for (Token t : tokens) {
            t.setEstadoToken(EstadoToken.INVALIDO);
        }
        tokenRepository.saveAll(tokens);
    }

    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepo.findByDni(request.getDni()).isPresent()) {
            throw new IllegalArgumentException("DNI ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setDni(request.getDni());
        usuario.setNombreCompleto(request.getNombreCompleto());
        usuario.setContrasena(passwordEncoder.encode(request.getContrasena()));
        usuario.setTurno(request.getTurno());

        try {
            usuario.setRol(Rol.valueOf(request.getRol().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rol inválido");
        }

        if (request.getHorarioEntrada() != null) {
            usuario.setHorarioEntrada(LocalTime.parse(request.getHorarioEntrada()));
        }
        if (request.getHorarioSalida() != null) {
            usuario.setHorarioSalida(LocalTime.parse(request.getHorarioSalida()));
        }

        // Guardar usuario y obtener el objeto guardado con ID
        Usuario usuarioGuardado = usuarioRepo.save(usuario);

        System.out.println("Se guarda el usuario :D");
        // Generar token JWT con el DNI guardado
        String jwt = jwtUtil.generarToken(usuarioGuardado.getDni());

        Token token = new Token();
        token.setToken(jwt);
        token.setUsuario(usuarioGuardado);
        token.setTipoToken(TipoToken.BEARER);
        token.setEstadoToken(EstadoToken.VALIDO);

        tokenRepository.save(token);

        AuthResponse response = new AuthResponse();
        response.setToken(jwt);

        return response;
    }
}