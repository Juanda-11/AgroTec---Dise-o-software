package com.agrotec.repository;

import com.agrotec.domain.Actividad;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ActividadRepository extends MongoRepository<Actividad, String> {

    List<Actividad> findByCultivoIdAndUsuarioIdOrderByFechaDesc(String cultivoId, String usuarioId);

    Optional<Actividad> findByIdAndUsuarioId(String id, String usuarioId);

    Optional<Actividad> findByUsuarioIdAndClienteId(String usuarioId, String clienteId);

    List<Actividad> findByUsuarioIdAndCompletadaFalseAndRecordatorioNotNullOrderByRecordatorioAsc(String usuarioId);

    List<Actividad> findByRecordatorioLessThanEqualAndCompletadaFalseAndRecordatorioEnviadoFalse(Instant ahora);

    List<Actividad> findByUsuarioIdAndActualizadoEnAfter(String usuarioId, Instant desde);

    void deleteByCultivoId(String cultivoId);

    void deleteByUsuarioId(String usuarioId);
}
