package quantify.BoticaSaid.dto;

public class UsuarioDto {
    private String dni;
    private String nombreCompleto;
    private String rol;
    private String horarioEntrada;
    private String horarioSalida;
    private String turno;

    public UsuarioDto() {}

    public UsuarioDto(String dni, String nombreCompleto, String rol, String horarioEntrada, String horarioSalida, String turno) {
        this.dni = dni;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.horarioEntrada = horarioEntrada;
        this.horarioSalida = horarioSalida;
        this.turno = turno;
    }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public String getHorarioEntrada() { return horarioEntrada; }
    public void setHorarioEntrada(String horarioEntrada) { this.horarioEntrada = horarioEntrada; }

    public String getHorarioSalida() { return horarioSalida; }
    public void setHorarioSalida(String horarioSalida) { this.horarioSalida = horarioSalida; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }
}