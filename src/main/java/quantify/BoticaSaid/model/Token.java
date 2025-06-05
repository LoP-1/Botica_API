package quantify.BoticaSaid.model;

import jakarta.persistence.*;
import quantify.BoticaSaid.enums.EstadoToken;
import quantify.BoticaSaid.enums.TipoToken;

@Entity
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String token;

    @Enumerated(EnumType.STRING)
    private EstadoToken estadoToken = EstadoToken.VALIDO;

    @Enumerated(EnumType.STRING)
    private TipoToken tipoToken = TipoToken.BEARER;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public TipoToken getTipoToken() {
        return tipoToken;
    }

    public void setTipoToken(TipoToken tipoToken) {
        this.tipoToken = tipoToken;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public EstadoToken getEstadoToken() {
        return estadoToken;
    }

    public void setEstadoToken(EstadoToken estadoToken) {
        this.estadoToken = estadoToken;
    }
}

