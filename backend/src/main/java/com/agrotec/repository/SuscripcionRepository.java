package com.agrotec.repository;

import com.agrotec.domain.Suscripcion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuscripcionRepository extends MongoRepository<Suscripcion, String> {

    List<Suscripcion> findByUsuarioIdOrderByInicioDesc(String usuarioId);

    Optional<Suscripcion> findFirstByUsuarioIdAndCanceladaFalseOrderByFinDesc(String usuarioId);

    void deleteByUsuarioId(String usuarioId);
}
