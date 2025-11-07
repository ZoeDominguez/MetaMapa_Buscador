package com.metamapa.buscador.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

        Query textQuery = TextQuery.queryText(criteria)
                .sortByScore()
                .with(pageable);

        textQuery.addCriteria(Criteria.where("deleted").is(false));
        if (StringUtils.hasText(tag)) {
            textQuery.addCriteria(Criteria.where("tags").is(tag));
        }

        List<Resultados_Documento> docs = mongoTemplate.find(textQuery, Resultados_Documento.class);

        Query countQuery = TextQuery.queryText(criteria);
        countQuery.addCriteria(Criteria.where("deleted").is(false));
        if (StringUtils.hasText(tag)) {
            countQuery.addCriteria(Criteria.where("tags").is(tag));
        }
        long total = mongoTemplate.count(countQuery, Resultados_Documento.class);

        List<BusquedaDTO> dtos = docs.stream().map(this::convertirADTO).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, total);
    }

    private BusquedaDTO convertirADTO(Resultados_Documento doc) {
        BusquedaDTO dto = new BusquedaDTO();
        dto.setId(doc.getId());
        dto.setNombre(doc.getNombre());
        dto.setDescripcion(doc.getDescripcion());
        dto.setTags(doc.getTags());
        return dto;
    }
}
