package com.pdf.indexador.service;

import com.pdf.indexador.domain.RagEmbedding;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmbeddingService {

    public RagEmbedding generateEmbedding(String fileName, List<String> pages) {
        return new RagEmbedding(
                UUID.randomUUID(),
                pages.size(),
                String.join("\n", pages),
                fileName
        );
    }
}