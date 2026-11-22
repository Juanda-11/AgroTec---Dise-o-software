package com.agrotec.repository;

import com.agrotec.domain.CategoriaPrecio;
import com.agrotec.domain.PrecioMercado;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrecioRepository extends MongoRepository<PrecioMercado, String> {

    Optional<PrecioMercado> findTopByOrderByFechaDesc();

    List<PrecioMercado> findByCategoriaAndPlazaAndFecha(CategoriaPrecio categoria, String plaza, LocalDate fecha);

    Optional<PrecioMercado> findByProductoIgnoreCaseAndPlazaAndFecha(String producto, String plaza, LocalDate fecha);

    List<PrecioMercado> findByProductoIgnoreCaseAndPlazaAndFechaGreaterThanEqualOrderByFechaAsc(
            String producto, String plaza, LocalDate desde);

    List<PrecioMercado> findByPlazaAndFecha(String plaza, LocalDate fecha);
}
