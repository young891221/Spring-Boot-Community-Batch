package com.community.batch.jobs;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.jobs.common.readers.QueueItemReader;
import com.community.batch.jobs.inactive.InactiveItemProcessor;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import lombok.AllArgsConstructor;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Configuration
@AllArgsConstructor
public class InactiveUserJobConfig {

    private final QueueItemReader<User> inactiveUserReader;
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final UserRepository userRepository;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job inactiveUserJob() {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .start(inactiveJobStep())
                .build();
    }

    private Step inactiveJobStep() {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User> chunk(10)
                //.readerIsTransactionalQueue()
                .reader(inactiveUserReader)
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .build();
    }

    @Bean
    @StepScope
    public QueueItemReader<User> inactiveUserReader(@Value("#{jobParameters[nowDate]}") Date nowDate) {
        LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());
        List<User> oldUsers = userRepository.findByUpdatedDateBeforeAndStatusEquals(now.minusYears(1), UserStatus.ACTIVE);
        return new QueueItemReader<>(oldUsers);
    }

    private InactiveItemProcessor inactiveUserProcessor() {
        return new InactiveItemProcessor();
    }

    private JpaItemWriter<User> inactiveUserWriter() {
        JpaItemWriter jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }
}
