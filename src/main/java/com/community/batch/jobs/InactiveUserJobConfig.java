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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Configuration
public class InactiveUserJobConfig {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob")
                .start(inactiveJobStep())
                .build();
    }

    private Step inactiveJobStep() {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User> chunk(10)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    private ItemReader<User> inactiveUserReader() {
        return () -> userRepository.findByCreatedDateBefore(LocalDateTime.now().minusYears(1));
    }

    private ItemProcessor<User, User> inactiveUserProcessor() {
        return User::setInactive;
    }

    private ItemWriter<User> inactiveUserWriter() {
        return ((List<? extends User> users) -> userRepository.saveAll(users));
    }
}
