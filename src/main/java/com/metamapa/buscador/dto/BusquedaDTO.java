package com.metamapa.buscador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.metamapa.buscador.model.Resultados_Documento;


@Data
@NoArgsConstructor
public class BusquedaDTO {

    private String id;

    private String titulo;

    private String descripcion;

    private List<String> etiquetas;

    public static BusquedaDTO from(Resultados_Documento doc) {
        BusquedaDTO dto = new BusquedaDTO();
        dto.id = doc.getId();
        dto.titulo = doc.getTitulo();
        dto.descripcion = doc.getDescripcion();
        dto.etiquetas = doc.getEtiquetas();
        return dto;
    }
}
