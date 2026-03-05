package com.pdf.indexador.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchJobLauncherConfig {

    @Bean
    public CommandLineRunner runJob(JobLauncher jobLauncher, Job pdfJob) {
        return args -> {
            jobLauncher.run(pdfJob, new JobParametersBuilder()
                    .addLong("time", System.currentTimeMillis())
                    .toJobParameters());
        };
    }
}