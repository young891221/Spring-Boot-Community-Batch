package com.community.batch.jobs;

import com.community.batch.domain.User;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import javax.persistence.EntityManagerFactory;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Configuration
public class InactiveUserJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Bean
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob")
                .start(inactiveJobStep())
                .build();
    }

    private Step inactiveJobStep() {
        return stepBuilderFactory.get("inactiveUserStep")
                .<List<User>, List<User>> chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    private ItemReader<List<User>> inactiveUserReader() {
        return () -> userRepository.findAll();
    }

    private ItemProcessor<List<User>, List<User>> inactiveUserProcessor() {
        return (List<User> users) -> users;
    }

    private ItemWriter<List<User>> inactiveUserWriter() {
        JpaItemWriter<List<User>> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }
}
