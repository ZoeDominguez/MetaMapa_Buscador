package com.metamapa.buscador.sync;

import com.metamapa.buscador.model.Resultados_Documento;
import com.metamapa.buscador.repository.ResultadosDocumentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


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

    /** ✔ Se ejecuta cuando la app está completamente levantada */
    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void syncInicial() {
        new Thread(() -> {
            try {
                System.out.println(">>> App levantada. Esperando 5 segundos antes de sincronizar...");
                Thread.sleep(5000);
                sincronizar();
            } catch (Exception e) {
                System.err.println("Error en sincronización inicial: " + e.getMessage());
            }
        }).start();
    }

    /** ✔ Se ejecuta cada 5 minutos */
    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncPeriodica() {
        System.out.println(">>> Ejecutando sincronización periódica...");
        sincronizar();
    }

    private void sincronizar() {
        try {
            String url = "https://two025-tp-entrega-2-zoedominguez-bsuh.onrender.com/hechos";
            Resultados_Documento[] respuesta = restTemplate.getForObject(url, Resultados_Documento[].class);

            if (respuesta == null) {
                System.err.println("El agregador devolvió null.");
                return;
            }

            List<Resultados_Documento> lista = Arrays.asList(respuesta);
            int nuevos = 0;

            for (Resultados_Documento doc : lista) {
                if (repo.existsById(doc.getId())) continue;
                if (repo.existsByNombre(doc.getNombre())) continue;

                repo.save(doc);
                nuevos++;
            }

            System.out.println("✔ Sincronización completada. Nuevos agregados: " + nuevos);

        } catch (Exception e) {
            System.err.println("Error sincronizando desde agregador: " + e.getMessage());
        }
    }
}
