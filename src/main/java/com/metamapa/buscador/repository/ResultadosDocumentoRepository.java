package com.metamapa.buscador.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.metamapa.buscador.model.Resultados_Documento;

public interface ResultadosDocumentoRepository extends MongoRepository<Resultados_Documento, String> {

    boolean existsByNombre(String nombre);
}
