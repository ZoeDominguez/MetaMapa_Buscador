package com.metamapa.buscador.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.metamapa.buscador.model.Resultados_Documento;


@Data
@NoArgsConstructor
public class BusquedaDTO {

    private String id;

    private String nombre;

    private String descripcion;

    private List<String> tags;

    public static BusquedaDTO from(Resultados_Documento doc) {
        BusquedaDTO dto = new BusquedaDTO();
        dto.id = doc.getId();
        dto.nombre = doc.getNombre();
        dto.descripcion = doc.getDescripcion();
        dto.tags = doc.getTags();
        return dto;
    }
}
