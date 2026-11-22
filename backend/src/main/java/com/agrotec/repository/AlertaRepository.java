package com.agrotec.repository;

import com.agrotec.domain.Alerta;
import com.agrotec.domain.TipoAlerta;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertaRepository extends MongoRepository<Alerta, String> {

    List<Alerta> findByUsuarioIdOrderByCreadaEnDesc(String usuarioId);

    List<Alerta> findByUsuarioIdAndTipoOrderByCreadaEnDesc(String usuarioId, TipoAlerta tipo);

    Optional<Alerta> findByIdAndUsuarioId(String id, String usuarioId);

    boolean existsByUsuarioIdAndClave(String usuarioId, String clave);

    long countByUsuarioIdAndLeidaFalse(String usuarioId);

    List<Alerta> findByUsuarioIdAndCreadaEnAfter(String usuarioId, Instant desde);

    void deleteByUsuarioId(String usuarioId);
}
