package com.metamapa.buscador.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.metamapa.buscador.client.AgregadorClient;
import com.metamapa.buscador.model.Resultados_Documento;
import com.metamapa.buscador.repository.ResultadosDocumentoRepository;

import jakarta.annotation.PostConstruct;

@Service
public class SyncService {

    private final AgregadorClient agregadorClient;
    private final ResultadosDocumentoRepository repo;

    public SyncService(AgregadorClient agregadorClient, ResultadosDocumentoRepository repo) {
        this.agregadorClient = agregadorClient;
        this.repo = repo;
    }

    @PostConstruct
    public void syncInicial() {
        sincronizar();
    }

    @Scheduled(fixedRate = 5 * 60 * 1000)
    public void syncPeriodico() {
        sincronizar();
    }

    private void sincronizar() {

        try {
            List<Resultados_Documento> hechos = agregadorClient.obtenerHechos();

            int nuevos = 0;

            for (Resultados_Documento doc : hechos) {

                // Si ya existe por ID, saltar
                if (doc.getId() != null && repo.existsById(doc.getId()))
                    continue;

                // Si existe por nombre, también saltar
                if (repo.existsByNombre(doc.getNombre()))
                    continue;

                repo.save(doc);
                nuevos++;
            }

            System.out.println("Sync OK - Nuevos agregados: " + nuevos);

        } catch (Exception e) {
            System.err.println("Error en sincronización: " + e.getMessage());
        }
    }
}
