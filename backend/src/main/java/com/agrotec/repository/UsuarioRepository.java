package com.agrotec.repository;

import com.agrotec.domain.Usuario;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByCelular(String celular);

    boolean existsByCelular(String celular);
}
