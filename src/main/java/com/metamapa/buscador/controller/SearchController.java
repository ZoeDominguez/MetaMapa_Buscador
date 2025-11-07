package com.metamapa.buscador.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.metamapa.buscador.dto.BusquedaDTO;
import com.metamapa.buscador.service.SearchSevice;

@RestController
@RequestMapping("/api/buscador")
public class SearchController {

    @Autowired
    private SearchSevice searchService;

    @GetMapping("/search")
    public ResponseEntity<Page<BusquedaDTO>> search(
            @RequestParam("q") String keyword,
            @RequestParam(value = "tag", required = false) String tag,
            Pageable pageable) { 
        
        Page<BusquedaDTO> results = searchService.buscar(keyword, tag, pageable);
        return ResponseEntity.ok(results);
    }
}