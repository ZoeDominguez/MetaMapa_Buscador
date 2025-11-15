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

    private String nombre_coleccion;

    private String titulo;    
    
    private String descripcion;    

    private List<String> etiquetas;

    private String categoria;

    private String ubicacion;

    private String fecha;

    private String origen;

    private boolean ocultoPorSolicitud = false;

}
