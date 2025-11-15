package com.metamapa.buscador.sync;

import com.metamapa.buscador.client.AgregadorClient; // Asegúrate de importar el cliente
import com.metamapa.buscador.model.Resultados_Documento;
import com.metamapa.buscador.dto.PdiFuenteDTO;
import com.metamapa.buscador.repository.ResultadosDocumentoRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
public class SyncScheduler {

    private final AgregadorClient agregadorClient;
    private final ResultadosDocumentoRepository repo;

    public SyncScheduler(ResultadosDocumentoRepository repo, AgregadorClient agregadorClient) {
        this.repo = repo;
        this.agregadorClient = agregadorClient;
    }

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

    @Scheduled(fixedRate = 2 * 60 * 1000)
    public void syncPeriodica() {
        System.out.println(">>> Ejecutando sincronización periódica...");
        sincronizar();
    }

    private void sincronizar() {
        try {
            // Obtener Hechos
            List<Resultados_Documento> hechos = agregadorClient.obtenerHechos();
            if (hechos.isEmpty()) {
                System.out.println("No se encontraron hechos para sincronizar.");
                return;
            }

            // Obtener PDIs
            List<PdiFuenteDTO> pdis = agregadorClient.obtenerPdis();
            System.out.println("PDIs encontrados: " + pdis.size());

            // Agrupar PDIs por su hechoId para acceso rápido
            Map<String, List<PdiFuenteDTO>> pdisPorHecho = pdis.stream()
                    .filter(p -> p.getHechoId() != null)
                    .collect(Collectors.groupingBy(PdiFuenteDTO::getHechoId));

            int procesados = 0;

            for (Resultados_Documento doc : hechos) {
                
                // Inicializar listas y buffers
                StringBuilder sbInfoPdi = new StringBuilder();
                StringBuilder sbInfoExterna = new StringBuilder();
                List<String> etiquetasHecho = doc.getEtiquetas() != null ? doc.getEtiquetas() : new ArrayList<>();

                // Buscar PDIs relacionados a este hecho
                List<PdiFuenteDTO> misPdis = pdisPorHecho.getOrDefault(doc.getId(), new ArrayList<>());

                for (PdiFuenteDTO pdi : misPdis) {
                    // Concatenar descripción y lugar del PDI para búsqueda
                    if (pdi.getDescripcion() != null) {
                        sbInfoPdi.append(pdi.getDescripcion()).append(" ");
                    }
                    if (pdi.getLugar() != null) {
                        sbInfoPdi.append(pdi.getLugar()).append(" ");
                    }

                    // Concatenar Texto de Imagen (OCR) para búsqueda
                    if (pdi.getTextoImagen() != null) {
                        sbInfoExterna.append(pdi.getTextoImagen()).append(" ");
                    }

                    // Fusionar etiquetas
                    if (pdi.getEtiquetas() != null) {
                        for (String tag : pdi.getEtiquetas()) {
                            if (!etiquetasHecho.contains(tag)) {
                                etiquetasHecho.add(tag);
                            }
                        }
                    }
                }

                // Asignar los datos procesados al documento del Hecho
                doc.setInfoPdi(sbInfoPdi.toString().trim());
                doc.setInfoExterna(sbInfoExterna.toString().trim());
                doc.setEtiquetas(etiquetasHecho);

                Optional<Resultados_Documento> existente = repo.findByTitulo(doc.getTitulo());

                if (existente.isPresent()) {
                    doc.setId(existente.get().getId());
                    System.out.println(">> Actualizando hecho existente por título: " + doc.getTitulo());
                } else {
                    System.out.println(">> Insertando nuevo hecho: " + doc.getTitulo());
                }

                // Guardar
                repo.save(doc);
                procesados++;
            }

            System.out.println("✔ Sincronización completada. Documentos procesados e indexados: " + procesados);

        } catch (Exception e) {
            System.err.println("Error crítico en proceso de sincronización: " + e.getMessage());
            e.printStackTrace();
        }
    }
}