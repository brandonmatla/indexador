package com.pdf.indexador.batch.writer;

import com.pdf.indexador.domain.RagEmbedding;
import org.postgresql.util.PGobject;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

@Component
public class RagWriter implements ItemWriter<List<RagEmbedding>> {

    private final JdbcTemplate jdbcTemplate;

    public RagWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends List<RagEmbedding>> chunk) {
        for (List<RagEmbedding> embeddings : chunk) {
            if (embeddings.isEmpty()) continue;

            RagEmbedding first = embeddings.get(0);
            String documentName = first.documentName();
            String filePath = first.filePath(); // asegúrate que tu record tenga filePath
            File file = new File(filePath);

            try {
                // Hash y tamaño del archivo
                String fileHash = hashFile(file);
                long fileSize = file.length();

                // 1️⃣ Revisar si ya existe el documento por file_name
                UUID documentId = jdbcTemplate.query(
                        "SELECT id FROM rag_documents WHERE file_name = ?",
                        new Object[]{documentName},
                        rs -> rs.next() ? (UUID) rs.getObject("id") : UUID.randomUUID()
                );

                // 2️⃣ Si ya existía, eliminar embeddings antiguos
                jdbcTemplate.update(
                        "DELETE FROM rag_embeddings WHERE document_id = ?",
                        documentId
                );

                // 3️⃣ Insertar o actualizar documento
                jdbcTemplate.update(
                        "INSERT INTO rag_documents (id, file_name, file_path, file_hash, file_size, status) " +
                                "VALUES (?, ?, ?, ?, ?, ?) " +
                                "ON CONFLICT (id) DO UPDATE SET " +
                                "file_path = EXCLUDED.file_path, " +
                                "file_hash = EXCLUDED.file_hash, " +
                                "file_size = EXCLUDED.file_size, " +
                                "status = EXCLUDED.status",
                        documentId,
                        documentName,
                        filePath,
                        fileHash,
                        fileSize,
                        "PROCESSED"
                );

                // 4️⃣ Insertar todos los embeddings nuevos
                for (RagEmbedding e : embeddings) {
                    StringBuilder sb = new StringBuilder("[");
                    float[] vector = e.embedding();
                    for (int i = 0; i < vector.length; i++) {
                        sb.append(vector[i]);
                        if (i < vector.length - 1) sb.append(",");
                    }
                    sb.append("]");

                    PGobject vectorObj = new PGobject();
                    vectorObj.setType("vector");
                    vectorObj.setValue(sb.toString());

                    jdbcTemplate.update(
                            "INSERT INTO rag_embeddings (id, document_id, chunk_index, content, embedding) VALUES (?, ?, ?, ?, ?)",
                            UUID.randomUUID(),
                            documentId,
                            e.chunkIndex(),
                            e.content(),
                            vectorObj
                    );
                }

                System.out.println("✅ Archivo " + documentName + " procesado con " + embeddings.size() + " embeddings.");

            } catch (Exception ex) {
                throw new RuntimeException("Error procesando archivo " + documentName, ex);
            }
        }
    }

    private String hashFile(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
        byte[] hash = digest.digest(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}