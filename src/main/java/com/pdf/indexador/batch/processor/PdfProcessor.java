package com.pdf.indexador.batch.processor;

import com.pdf.indexador.domain.RagEmbedding;
import com.pdf.indexador.service.EmbeddingService;
import com.pdf.indexador.service.PdfService;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class PdfProcessor implements ItemProcessor<File, RagEmbedding> {

    private final PdfService pdfService;
    private final EmbeddingService embeddingService;

    public PdfProcessor(PdfService pdfService, EmbeddingService embeddingService) {
        this.pdfService = pdfService;
        this.embeddingService = embeddingService;
    }

    @Override
    public RagEmbedding process(File file) {
        List<String> pages = pdfService.extractPages(file);
        return embeddingService.generateEmbedding(file.getName(), pages);
    }
}