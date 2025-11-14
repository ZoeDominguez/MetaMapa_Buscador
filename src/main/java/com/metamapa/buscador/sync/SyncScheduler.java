package com.metamapa.buscador.sync;

import com.metamapa.buscador.model.Resultados_Documento;
import com.metamapa.buscador.repository.ResultadosDocumentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;

@Component
public class SyncScheduler {

    private final RestTemplate restTemplate;
    private final ResultadosDocumentoRepository repo;

    public SyncScheduler(ResultadosDocumentoRepository repo) {
        this.restTemplate = new RestTemplate();
        this.repo = repo;
    }

    /** ✔ Se ejecuta cuando inicia el Buscador */
    @PostConstruct
    public void syncInicial() {
        System.out.println(">>> Ejecutando sincronización inicial...");
        sincronizar();
    }

    /** ✔ Se ejecuta cada 5 minutos */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncPeriodica() {
        System.out.println(">>> Ejecutando sincronización periódica...");
        sincronizar();
    }

    private void sincronizar() {
        try {
            // 1. Llamar al agregador
            String url = "https://two025-tp-entrega-2-zoedominguez-bsuh.onrender.com/hechos";
            Resultados_Documento[] respuesta = restTemplate.getForObject(url, Resultados_Documento[].class);

            if (respuesta == null) {
                System.err.println("El agregador devolvió null.");
                return;
            }

            List<Resultados_Documento> lista = Arrays.asList(respuesta);

            int nuevos = 0;

            // 2. Guardar solo los documentos nuevos
            for (Resultados_Documento doc : lista) {

                // si ya existe por ID → continuar
                if (repo.existsById(doc.getId()))
                    continue;

                // si existe por nombre (si tu agregador usa nombres únicos)
                if (repo.existsByNombre(doc.getNombre()))
                    continue;

                repo.save(doc);
                nuevos++;
            }

            System.out.println("✔ Sincronización completada. Nuevos agregados: " + nuevos);

        } catch (Exception e) {
            System.err.println("Error sincronizando desde agregador: " + e.getMessage());
        }
    }
}
