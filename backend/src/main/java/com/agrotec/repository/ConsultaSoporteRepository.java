package com.agrotec.repository;

import com.agrotec.domain.ConsultaSoporte;
import com.agrotec.domain.EstadoConsulta;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConsultaSoporteRepository extends MongoRepository<ConsultaSoporte, String> {

    List<ConsultaSoporte> findByUsuarioIdOrderByCreadaEnDesc(String usuarioId);

    Optional<ConsultaSoporte> findByIdAndUsuarioId(String id, String usuarioId);

    List<ConsultaSoporte> findByEstadoOrderByCreadaEnAsc(EstadoConsulta estado);

    void deleteByUsuarioId(String usuarioId);
}
