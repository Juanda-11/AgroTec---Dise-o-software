package com.agrotec.repository;

import com.agrotec.domain.Capacitacion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CapacitacionRepository extends MongoRepository<Capacitacion, String> {
}
