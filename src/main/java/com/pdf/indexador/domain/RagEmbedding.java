package com.pdf.indexador.domain;

import java.util.UUID;

public record RagEmbedding(
        UUID documentId,
        int chunkIndex,
        String content,
        String documentName
) {}