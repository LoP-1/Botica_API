package quantify.BoticaSaid.dto;

public class UsuarioDto {
    private String dni;
    private String nombreCompleto;
    private String rol;

    public UsuarioDto() {}

    public UsuarioDto(String dni, String nombreCompleto, String rol) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}