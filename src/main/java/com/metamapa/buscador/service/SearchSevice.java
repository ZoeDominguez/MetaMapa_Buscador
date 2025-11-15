package com.metamapa.buscador.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.metamapa.buscador.dto.BusquedaDTO;
import com.metamapa.buscador.model.Resultados_Documento;

@Service
public class SearchSevice {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Page<BusquedaDTO> buscar(String keyword, String tag, Pageable pageable) {
       
        TextCriteria criteria = TextCriteria.forDefaultLanguage().matching(keyword);
        Query textQuery = TextQuery.queryText(criteria).sortByScore().with(pageable);

        textQuery.addCriteria(Criteria.where("ocultoPorSolicitud").ne(true));


        if (tag != null) {
            textQuery.addCriteria(Criteria.where("etiquetas").is(tag));
        }
        


        List<Resultados_Documento> docs = mongoTemplate.find(textQuery, Resultados_Documento.class);

        Query countQuery = TextQuery.queryText(criteria);

        countQuery.addCriteria(Criteria.where("ocultoPorSolicitud").ne(true));

        if (StringUtils.hasText(tag)) {
            countQuery.addCriteria(Criteria.where("etiquetas").is(tag));
        }
        long total = mongoTemplate.count(countQuery, Resultados_Documento.class);

        List<BusquedaDTO> dtos = docs.stream().map(this::convertirADTO).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    public void ocultarResultadosPorHecho(String hechoId) {
        Query query = new Query(Criteria.where("hechoId").is(hechoId));
        Update update = new Update().set("ocultoPorSolicitud", true);

        mongoTemplate.updateMulti(query, update, Resultados_Documento.class);
        
    }

    private BusquedaDTO convertirADTO(Resultados_Documento doc) {
        BusquedaDTO dto = new BusquedaDTO();
        dto.setId(doc.getId());
        dto.setTitulo(doc.getTitulo());
        dto.setDescripcion(doc.getDescripcion());
        dto.setEtiquetas(doc.getEtiquetas());
        return dto;
    }
}
