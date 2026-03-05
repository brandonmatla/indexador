package com.pdf.indexador.domain;

import java.util.UUID;

public record RagDocument(
        UUID id,
        String fileName
) {}