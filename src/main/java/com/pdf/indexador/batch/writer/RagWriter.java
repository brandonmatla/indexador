package com.pdf.indexador.batch.writer;

import com.pdf.indexador.domain.RagEmbedding;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RagWriter implements ItemWriter<RagEmbedding> {

    private final JdbcTemplate jdbcTemplate;

    public RagWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends RagEmbedding> chunk) {
        for (RagEmbedding e : chunk) {
            jdbcTemplate.update(
                    "INSERT INTO rag_embeddings (id, document_id, chunk_index, content) VALUES (?, ?, ?, ?)",
                    UUID.randomUUID(),
                    e.documentId(),
                    e.chunkIndex(),
                    e.content()
            );
            System.out.println("✅ Guardado embedding de: " + e.documentName());
        }
    }
}