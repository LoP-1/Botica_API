package quantify.BoticaSaid.dto;

public class AuthResponse {
    private String token;
    private UsuarioDto usuario;

    public AuthResponse() {
    }

    public AuthResponse(String token, UsuarioDto usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioDto getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioDto usuario) {
        this.usuario = usuario;
    }
}