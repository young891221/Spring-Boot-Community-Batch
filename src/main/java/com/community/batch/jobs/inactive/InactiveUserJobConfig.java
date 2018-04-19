package com.community.batch.jobs.inactive;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.jobs.inactive.listener.InactiveChunkListener;
import com.community.batch.jobs.inactive.listener.InactiveIJobListener;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import javax.persistence.EntityManagerFactory;

import lombok.AllArgsConstructor;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Configuration
@AllArgsConstructor
public class InactiveUserJobConfig {
    private final static int CHUNK_SIZE = 5;

    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job inactiveUserJob(JobBuilderFactory jobBuilderFactory, InactiveIJobListener inactiveIJobListener, Flow inactiveJobFlow) {
        return jobBuilderFactory.get("inactiveUserJob")
                .preventRestart()
                .listener(inactiveIJobListener)
                .start(inactiveJobFlow)
                .end()
                .build();
    }

    @Bean
    public Flow multiFlow(Step inactiveJobStep) {
        Flow flows[] = new Flow[5];
        IntStream.range(0, flows.length).forEach(i -> flows[i] = new FlowBuilder<Flow>("MultiFlow"+i).from(inactiveJobFlow(inactiveJobStep)).end());

        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("MultiFlowTest");
        return flowBuilder
                .split(taskExecutor())
                .add(flows)
                .build();
    }

    //빈으로 생성하면 싱글톤이기 때문에 멀티 Flow를 위해서는 빈으로 등록하면 안됨
    private Flow inactiveJobFlow(Step inactiveJobStep) {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("inactiveJobFlow");
        return flowBuilder
                .start(new InactiveJobExecutionDecider())
                .on(FlowExecutionStatus.FAILED.getName()).end()
                .on(FlowExecutionStatus.COMPLETED.getName()).to(inactiveJobStep)
                .end();
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
    public ListItemReader<User> inactiveUserReader(@Value("#{jobParameters[nowDate]}") Date nowDate, UserRepository userRepository) {
        LocalDateTime now = LocalDateTime.ofInstant(nowDate.toInstant(), ZoneId.systemDefault());
        List<User> inactiveUsers = userRepository.findByCreatedDateBeforeAndStatusEquals(now.minusYears(1), UserStatus.ACTIVE);
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
    public TaskExecutor taskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }
}
