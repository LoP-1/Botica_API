package quantify.BoticaSaid.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quantify.BoticaSaid.enums.EstadoToken;
import quantify.BoticaSaid.model.Token;
import quantify.BoticaSaid.model.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    List<Token> findAllByUsuarioAndEstadoToken(Usuario usuario, EstadoToken estadoToken);


    void deleteAllByUsuario_Id(Long usuarioId);
}

