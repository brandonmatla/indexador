package com.pdf.indexador.service;

import com.pdf.indexador.domain.RagEmbedding;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class EmbeddingService {

    private static final int CHUNK_SIZE = 1000; // cantidad de caracteres por chunk (ajusta según necesites)

    // Ahora recibimos también la ruta completa del archivo
    public List<RagEmbedding> generateEmbeddings(String fileName, String filePath, List<String> pages) {
        List<RagEmbedding> embeddings = new ArrayList<>();
        String content = String.join("\n", pages);

        // dividir contenido en chunks
        int chunkIndex = 0;
        for (int start = 0; start < content.length(); start += CHUNK_SIZE) {
            int end = Math.min(start + CHUNK_SIZE, content.length());
            String chunkContent = content.substring(start, end);

            float[] vector = createEmbedding(chunkContent); // genera embedding

            // Creamos el RagEmbedding con los 6 parámetros correctos
            embeddings.add(new RagEmbedding(
                    UUID.randomUUID(),
                    chunkIndex,
                    chunkContent,
                    fileName,
                    filePath,
                    vector
            ));
            chunkIndex++;
        }

        return embeddings;
    }

    // método de ejemplo para generar un embedding falso (float[])
    private float[] createEmbedding(String text) {
        int dim = 768; // dimensión típica de embeddings de OpenAI
        float[] embedding = new float[dim];
        for (int i = 0; i < dim; i++) {
            embedding[i] = (float) Math.random(); // temporal: reemplaza por tu modelo real
        }
        return embedding;
    }
}