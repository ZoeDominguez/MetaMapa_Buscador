package com.metamapa.buscador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PdiFuenteDTO {

    private String id;

    @JsonProperty("hecho_id")
    private String hechoId;

    private String descripcion;

    private String lugar;

    private Object momento; 

    @JsonProperty("url_imagen")
    private String urlImagen;

    @JsonProperty("texto_imagen") 
    private String textoImagen; 

    private List<String> etiquetas;
}