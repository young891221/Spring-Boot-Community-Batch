package com.community.batch.jobs.inactive;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.Grade;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.jobs.inactive.listener.InactiveChunkListener;
import com.community.batch.jobs.inactive.listener.InactiveIJobListener;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Slf4j
@Configuration
@AllArgsConstructor
public class InactiveUserJobConfig {
    private final static int CHUNK_SIZE = 5;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, InactiveIJobListener inactiveIJobListener, Step parititionerStep) {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .listener(inactiveIJobListener)
                .start(parititionerStep)
                .build();
    }

    @Bean
    @JobScope
    public Step parititionerStep(StepBuilderFactory stepBuilderFactory, Step inactiveJobStep) {
        return stepBuilderFactory
                .get("parititionerStep")
                .partitioner("parititionerStep", new InactiveUserPartitioner())
                .gridSize(10)
                .step(inactiveJobStep)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step inactiveJobStep(StepBuilderFactory stepBuilderFactory, ListItemReader<User> inactiveUserReader, InactiveChunkListener inactiveChunkListener) {
        return stepBuilderFactory.get("inactiveUserStep")
                .<User, User> chunk(CHUNK_SIZE)
                .reader(inactiveUserReader)
                .processor(inactiveUserProcessor())
                .writer(inactiveUserWriter())
                .listener(inactiveChunkListener)
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<User> inactiveUserReader(@Value("#{stepExecutionContext[grade]}") String grade, UserRepository userRepository) {
        log.info(Thread.currentThread().getName());
        List<User> inactiveUsers = userRepository.findByCreatedDateBeforeAndStatusEqualsAndGradeEquals(LocalDateTime.now().minusYears(1), UserStatus.ACTIVE, Grade.valueOf(grade));
        return new ListItemReader<>(inactiveUsers);
    }

    /*@Bean(destroyMethod="")
    @StepScope
    public JpaPagingItemReader<User> inactiveUserJpaReader(@Value("#{jobParameters[nowDate]}") Date nowDate) {
        JpaPagingItemReader<User> jpaPagingItemReader = new JpaPagingItemReader<>();
        jpaPagingItemReader.setQueryString("select u from User as u where u.createdDate < :createdDate and u.status = :status");

        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());
        map.put("createdDate", now.minusYears(1));
        map.put("status", UserStatus.ACTIVE);

        jpaPagingItemReader.setParameterValues(map);
        jpaPagingItemReader.setEntityManagerFactory(entityManagerFactory);
        jpaPagingItemReader.setPageSize(CHUNK_SIZE);
        return jpaPagingItemReader;
    }*/

    private InactiveItemProcessor inactiveUserProcessor() {
        return new InactiveItemProcessor();
    }

    private JpaItemWriter<User> inactiveUserWriter() {
        JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    @Bean
    public TaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
