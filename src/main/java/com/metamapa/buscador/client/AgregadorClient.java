package com.metamapa.buscador.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamapa.buscador.dto.PdiFuenteDTO;
import com.metamapa.buscador.model.Resultados_Documento;

@Service
public class AgregadorClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AgregadorClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Configuramos un ObjectMapper propio para ser tolerante a fallos
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Resultados_Documento> obtenerHechos() {
        String url = "https://two025-tp-entrega-2-zoedominguez-bsuh.onrender.com/hechos";
        try {
            Resultados_Documento[] arr = restTemplate.getForObject(url, Resultados_Documento[].class);
            return arr != null ? Arrays.asList(arr) : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Error obteniendo hechos: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<PdiFuenteDTO> obtenerPdis() {
        String url = "https://tpa-ddsi-2025-grupo-9-fuentes.onrender.com/api/pdis";
        
        try {
            // 1. Forzar cabecera JSON
            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept", "application/json");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // 2. Obtener como String para depuración (evita el error "Error extracting response")
            ResponseEntity<String> response = restTemplate.exchange(
                url, 
                HttpMethod.GET, 
                entity, 
                String.class
            );

            String jsonRaw = response.getBody();
            
            // Imprimimos los primeros caracteres para ver si es JSON, XML o HTML de error
            if (jsonRaw != null) {
                System.out.println(">>> JSON PDIS RECIBIDO (primeros 200 cars): " + 
                    jsonRaw.substring(0, Math.min(jsonRaw.length(), 200)));
            } else {
                System.out.println(">>> JSON PDIS es NULL");
                return Collections.emptyList();
            }

            // 3. Mapear manualmente
            PdiFuenteDTO[] arr = objectMapper.readValue(jsonRaw, PdiFuenteDTO[].class);
            return Arrays.asList(arr);

        } catch (Exception e) {
            System.err.println("!!! Error crítico parseando PDIs: " + e.getMessage());
            // Si falla aquí, veremos en la consola el JSON RAW que causó el problema
            return Collections.emptyList();
        }
    }
}