package com.agrotec.repository;

import com.agrotec.domain.Cultivo;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CultivoRepository extends MongoRepository<Cultivo, String> {

    List<Cultivo> findByUsuarioIdOrderByActualizadoEnDesc(String usuarioId);

    Optional<Cultivo> findByIdAndUsuarioId(String id, String usuarioId);

    Optional<Cultivo> findByUsuarioIdAndClienteId(String usuarioId, String clienteId);

    List<Cultivo> findByUsuarioIdAndActualizadoEnAfter(String usuarioId, Instant desde);

    void deleteByUsuarioId(String usuarioId);
}
