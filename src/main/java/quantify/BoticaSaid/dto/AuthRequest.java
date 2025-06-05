package quantify.BoticaSaid.dto;

public class AuthRequest {
    private String dni;
    private String contrasena;

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContraseña(String contraseña) {
        this.contrasena = contraseña;
    }
}
