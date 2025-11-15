package com.metamapa.buscador.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.metamapa.buscador.model.Resultados_Documento;

public interface ResultadosDocumentoRepository extends MongoRepository<Resultados_Documento, String> {

    // Cambiamos de boolean a Optional para obtener el objeto si existe
    Optional<Resultados_Documento> findByTitulo(String titulo);
}