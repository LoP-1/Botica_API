package quantify.BoticaSaid.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import quantify.BoticaSaid.dto.AuthRequest;
import quantify.BoticaSaid.dto.AuthResponse;
import quantify.BoticaSaid.dto.RegisterRequest;
import quantify.BoticaSaid.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint para login
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        System.out.println("Intentando login para DNI: " + request.getDni());
        AuthResponse response = authService.login(request);
        System.out.println("Login exitoso, JWT generado");
        return ResponseEntity.ok(response);
    }

    // Endpoint para registro
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        System.out.println("Intentando registrar usuario con DNI: " + request.getDni());
        AuthResponse response = authService.register(request);
        System.out.println("Registro exitoso, JWT generado");
        return ResponseEntity.ok(response);
    }
}

