package com.agrotec.repository;

import com.agrotec.domain.Finca;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FincaRepository extends MongoRepository<Finca, String> {

    Optional<Finca> findByUsuarioId(String usuarioId);

    void deleteByUsuarioId(String usuarioId);
}
