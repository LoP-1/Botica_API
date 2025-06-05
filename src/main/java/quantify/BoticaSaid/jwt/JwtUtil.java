package quantify.BoticaSaid.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Clave secreta para firmar el token (debe tener suficiente longitud)
    private final Key key = Keys.hmacShaKeyFor("clave-secreta-muy-larga-para-firmar-tokens-jwt-1234567890".getBytes());

    // Tiempo de expiración: 24 horas (puedes ajustar)
    private final long expirationTime = 1000 * 60 * 60 * 24;

    // Generar token usando el DNI del usuario
    public String generarToken(String dni) {
        return Jwts.builder()
                .setSubject(dni)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // Validar token (verifica firma y expiración)
    public boolean validarToken(String token, String dni) {
        final String username = obtenerDni(token);
        return (username.equals(dni) && !estaExpirado(token));
    }

    // Obtener DNI del token
    public String obtenerDni(String token) {
        return obtenerClaim(token, Claims::getSubject);
    }

    // Obtener cualquier claim
    public <T> T obtenerClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = obtenerClaims(token);
        return claimsResolver.apply(claims);
    }

    // Obtener Claims
    private Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Verificar si token expiró
    private boolean estaExpirado(String token) {
        return obtenerClaims(token).getExpiration().before(new Date());
    }
}
