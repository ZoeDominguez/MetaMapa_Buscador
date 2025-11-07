package com.metamapa.buscador.sync;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;

import org.bson.Document;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.Arrays;

@Component
public class ChangeStreamSync {

    private final MongoClient mongoClient;

    public ChangeStreamSync(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @PostConstruct
    public void init() {
    
        MongoDatabase database = mongoClient.getDatabase("metamapa");

   
        startWatcher(database, "hechos", "hecho");
        startWatcher(database, "pdis", "pdi");
        startWatcher(database, "result_herramientas_externas", "externa");
    }

    private void startWatcher(MongoDatabase database, String collectionName, String tipoDocumento) {
        MongoCollection<Document> coll = database.getCollection(collectionName);

        MongoCursor<ChangeStreamDocument<Document>> cursor = coll.watch(Arrays.asList(
                        Aggregates.match(Filters.in("operationType",
                                Arrays.asList("insert", "update", "replace", "delete")))))
                .fullDocument(FullDocument.UPDATE_LOOKUP)
                .iterator();

        new Thread(() -> {
            try {
                MongoCollection<Document> resultDocs = database.getCollection("result_documentos");
                while (cursor.hasNext()) {
                    ChangeStreamDocument<Document> change = cursor.next();
                    Document full = change.getFullDocument();
                    String op = change.getOperationType().getValue();

                    if ("insert".equals(op) || "update".equals(op) || "replace".equals(op)) {
                        if (full == null) continue;

                        Document docToUpsert = new Document();
                        docToUpsert.put("nombre", full.getString("nombre"));
                        docToUpsert.put("descripcion", full.getString("descripcion"));
                        docToUpsert.put("tags", full.get("tags"));
                        docToUpsert.put("tipoDocumento", tipoDocumento);
                        docToUpsert.put("deleted", false);
                        docToUpsert.put("updatedAt", Instant.now().toString());

                        switch (tipoDocumento) {
                            case "hecho":
                                docToUpsert.put("infoPdi", full.get("infoPdi"));
                                docToUpsert.put("infoExterna", full.get("infoExterna"));
                                break;
                            case "pdi":
                                docToUpsert.put("infoUbicacion", full.get("ubicacion"));
                                break;
                            case "externa":
                                docToUpsert.put("origen", full.get("origen"));
                                docToUpsert.put("resultado", full.get("resultado"));
                                break;
                        }

                        resultDocs.updateOne(
                                Filters.eq("nombre", full.getString("nombre")),
                                new Document("$set", docToUpsert),
                                new UpdateOptions().upsert(true)
                        );

                        System.out.printf("[%s] Upsert en result_documentos: %s%n",
                                tipoDocumento, full.getString("nombre"));

                    } else if ("delete".equals(op)) {
                    
                        String id = change.getDocumentKey().getObjectId("_id").getValue().toHexString();
                        resultDocs.updateOne(
                                Filters.eq("_id", id),
                                Updates.combine(
                                        Updates.set("deleted", true),
                                        Updates.set("updatedAt", Instant.now().toString())
                                )
                        );

                        System.out.printf("[%s] Marcado como eliminado en result_documentos: %s%n",
                                tipoDocumento, id);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error en watcher de " + collectionName);
                e.printStackTrace();
            }
        }, "ChangeStream-" + collectionName).start();
    }
}
