package com.metamapa.buscador.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
import org.springframework.data.mongodb.core.index.TextIndexDefinition;

import com.metamapa.buscador.model.Resultados_Documento;

@Configuration
public class MongoConfig implements ApplicationRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        
        IndexOperations indexOps = mongoTemplate.indexOps(Resultados_Documento.class);

        System.out.println("Asegurando índice de texto en 'documentos_buscables'...");
        
        TextIndexDefinition textIndex = new TextIndexDefinition.TextIndexDefinitionBuilder()
                .onField("nombre", 3F)       
                .onField("descripcion", 2F)    
                .onField("infoPdi", 1F)       
                .onField("infoExterna", 1F)   
                .withDefaultLanguage("spanish")       
                .build();

        indexOps.ensureIndex(textIndex); 
        System.out.println("Asegurando índice único en 'nombre'...");
        
        Index uniqueNameIndex = new Index()
                .on("nombre", Sort.Direction.ASC) 
                .unique(); 
                                       
        indexOps.ensureIndex(uniqueNameIndex);

        System.out.println("Asegurando índice en tags y deleted...");
        indexOps.ensureIndex(new Index().on("tags", Sort.Direction.ASC));
        indexOps.ensureIndex(new Index().on("deleted", Sort.Direction.ASC));
    }
}