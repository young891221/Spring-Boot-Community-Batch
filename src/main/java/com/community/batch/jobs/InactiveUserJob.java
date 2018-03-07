package com.community.batch.jobs;

import com.community.batch.domain.User;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

    @Bean
    public ItemReader<List<User>> inactiveUserReader() {
        return () -> userRepository.findAll();
    }
}
