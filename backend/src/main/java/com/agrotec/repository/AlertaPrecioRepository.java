package com.agrotec.repository;

import com.agrotec.domain.AlertaPrecio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AlertaPrecioRepository extends MongoRepository<AlertaPrecio, String> {

    List<AlertaPrecio> findByUsuarioId(String usuarioId);

    Optional<AlertaPrecio> findByIdAndUsuarioId(String id, String usuarioId);

    List<AlertaPrecio> findByActivaTrue();

    void deleteByUsuarioId(String usuarioId);
}
