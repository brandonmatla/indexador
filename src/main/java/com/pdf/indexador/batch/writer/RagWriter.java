package com.pdf.indexador.batch.writer;

import com.pdf.indexador.domain.RagEmbedding;
import org.postgresql.util.PGobject;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class RagWriter implements ItemWriter<RagEmbedding> {

    private final JdbcTemplate jdbcTemplate;

    public RagWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends RagEmbedding> chunk) {
        for (RagEmbedding e : chunk) {
            // Convertimos float[] a String compatible con pgvector
            StringBuilder sb = new StringBuilder("[");
            float[] vector = e.embedding();
            for (int i = 0; i < vector.length; i++) {
                sb.append(vector[i]);
                if (i < vector.length - 1) {
                    sb.append(",");
                }
            }
            sb.append("]");

            PGobject vectorObj = new PGobject();
            try {
                vectorObj.setType("vector");
                vectorObj.setValue(sb.toString());
            } catch (Exception ex) {
                throw new RuntimeException("Error al crear PGobject para embedding", ex);
            }

            // Insertamos en la base de datos
            jdbcTemplate.update(
                    "INSERT INTO rag_embeddings (id, document_id, chunk_index, content, embedding) VALUES (?, ?, ?, ?, ?)",
                    UUID.randomUUID(),
                    e.documentId(),
                    e.chunkIndex(),
                    e.content(),
                    vectorObj
            );

            System.out.println("✅ Guardado embedding de: " + e.documentName());
        }
    }
}