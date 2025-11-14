package com.metamapa.buscador.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.metamapa.buscador.model.Resultados_Documento;

@Service
public class AgregadorClient {

    private final RestTemplate restTemplate;

    public AgregadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Resultados_Documento> obtenerHechos() {
        String url = "https://two025-tp-entrega-2-zoedominguez-bsuh.onrender.com/hechos";
        Resultados_Documento[] arr = restTemplate.getForObject(url, Resultados_Documento[].class);
        return Arrays.asList(arr);
    }
}
