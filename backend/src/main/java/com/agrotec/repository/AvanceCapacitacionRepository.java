package com.agrotec.repository;

import com.agrotec.domain.AvanceCapacitacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AvanceCapacitacionRepository extends MongoRepository<AvanceCapacitacion, String> {

    List<AvanceCapacitacion> findByUsuarioId(String usuarioId);

    Optional<AvanceCapacitacion> findByUsuarioIdAndCapacitacionId(String usuarioId, String capacitacionId);

    void deleteByUsuarioId(String usuarioId);
}
