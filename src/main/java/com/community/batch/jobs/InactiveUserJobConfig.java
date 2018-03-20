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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Configuration
@Transactional
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
                .<List<User>, List<User>> chunk(1)
                .reader(inactiveUserReader())
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    private ItemReader<List<User>> inactiveUserReader() {
        return () -> userRepository.findByCreatedDateBefore(LocalDateTime.now().minusYears(1)); //쿼리자체가 리스트형식을 반환하기에 리스트로 변경
    }

    private ItemProcessor<List<User>, List<User>> inactiveUserProcessor() {
        return users -> users.stream().peek(User::setInactive).collect(Collectors.toList());
    }

    private ItemWriter<List<User>> inactiveUserWriter() {
        return ((List<? extends List<User>> items) ->
                items.stream().forEach(users -> userRepository.saveAll(users)));
    }
}
