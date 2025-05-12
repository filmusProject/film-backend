package com.filmus.backend.movie.batch;

import com.filmus.backend.movie.entity.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MovieBatchConfig {

    private final MovieItemReader    reader;
    private final MovieItemProcessor processor;
    private final MovieItemWriter    writer;

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /* Step */
    @Bean
    public Step nlpKeywordStep() {
        return new StepBuilder("nlpKeywordStep", jobRepository)
                .<Movie,Movie>chunk(50, transactionManager)
                .reader(reader.movieReader())
                .processor(processor)
                .writer(writer)
                .build();
    }

    /* Job */
    @Bean
    public Job nlpKeywordJob() {
        return new JobBuilder("nlpKeywordJob", jobRepository)
                .start(nlpKeywordStep())
                .build();
    }
}