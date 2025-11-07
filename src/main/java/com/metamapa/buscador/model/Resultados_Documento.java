package com.metamapa.buscador.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "result_documentos")
public class Resultados_Documento {

    @Id
    private String id;

    private String nombre;

    private String descripcion;
    
    private String infoPdi;

    private String infoExterna;

    private List<String> tags;
    
    private boolean deleted = false;

    private String origen;

}
