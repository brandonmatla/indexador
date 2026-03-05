package com.pdf.indexador.batch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Iterator;
import java.util.List;

@Component
public class PdfFileItemReader implements ItemReader<File> {

    private final Iterator<File> iterator;

    public PdfFileItemReader() {
        File folder = new File("archivos");
        List<File> files = List.of(folder.listFiles(f -> f.getName().endsWith(".pdf")));
        this.iterator = files.iterator();
    }

    @Override
    public File read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}