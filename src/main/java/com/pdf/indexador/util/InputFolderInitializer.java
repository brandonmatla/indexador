package com.pdf.indexador.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class InputFolderInitializer {

    @Value("${indexador.pdf.input-folder}")
    private String inputFolder;

    @PostConstruct
    public void init() {
        try {
            Path path = Path.of(inputFolder);

            if (Files.notExists(path)) {
                Files.createDirectories(path);
                System.out.println("📁 Carpeta creada: " + path);
            } else {
                System.out.println("📁 Carpeta existente: " + path);
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "❌ No se pudo crear la carpeta de entrada de PDFs", e
            );
        }
    }
}