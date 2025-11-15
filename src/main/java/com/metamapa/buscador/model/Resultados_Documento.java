package com.metamapa.buscador.model;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "result_documentos")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Resultados_Documento {

    @Id
    private String id;

    private String nombre_coleccion;

    private String titulo;    
    
    private String descripcion;    

    private List<String> etiquetas;

    private String categoria;

    private String ubicacion;

    private String fecha;

    private String origen;

    private boolean ocultoPorSolicitud = false;
    
    // descripciones y lugares de los PdIs concatenados
    private String infoPdi; 

    // textos extraídos de imágenes (OCR) concatenados
    private String infoExterna; 
}