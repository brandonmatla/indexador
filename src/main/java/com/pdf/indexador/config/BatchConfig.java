package com.pdf.indexador.config;

import com.pdf.indexador.batch.processor.PdfProcessor;
import com.pdf.indexador.batch.reader.PdfFileItemReader;
import com.pdf.indexador.batch.writer.RagWriter;
import com.pdf.indexador.domain.RagEmbedding;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.File;
import java.util.List;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job pdfJob(JobRepository jobRepository, Step pdfStep) {
        return new JobBuilder("pdfJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(pdfStep)
                .build();
    }

    @Bean
    public Step pdfStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            PdfFileItemReader reader,
            PdfProcessor processor,
            RagWriter writer
    ) {
        return new StepBuilder("pdfStep", jobRepository)
                .<File, List<RagEmbedding>>chunk(1, transactionManager) // <File, List<RagEmbedding>>
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}