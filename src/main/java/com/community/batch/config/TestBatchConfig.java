package com.community.batch.config;

import com.community.batch.tasklet.HelloTasklet;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by KimYJ on 2018-03-06.
 */
@Configuration
public class TestBatchConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job simpleJob() {
        SimpleJobBuilder simpleJobBuilder = jobBuilderFactory
                .get("simpleJob")
                .start(step1());
        return simpleJobBuilder.build();
    }

    @Bean
    public Job commonJob() {
        return jobBuilderFactory.get("commonJob")
                .flow(step1())
                .next(step2())
                .end()
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("testStep1").tasklet(new HelloTasklet()).build();
    }

    @Bean
    public Step step2() {
        return stepBuilderFactory.get("testStep2").tasklet(new HelloTasklet()).build();
    }

    /*@Bean
    public Step step3(UserReader userReader) {
        return stepBuilderFactory.get("testStep2")
                .chunk(3)
                .reader(userReader)
                .processor()
                .writer()
                .build();
    }*/
}
