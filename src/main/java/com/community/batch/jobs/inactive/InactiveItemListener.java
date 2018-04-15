package com.community.batch.jobs.inactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterChunk;
import org.springframework.batch.core.annotation.BeforeChunk;
import org.springframework.stereotype.Component;

/**
 * Created by young891221@gmail.com on 2018-04-15
 * Blog : http://haviyj.tistory.com
 * Github : http://github.com/young891221
 */
@Slf4j
@Component
public class InactiveItemListener implements JobExecutionListener {

    public void beforeJob(JobExecution jobExecution) {
        log.info("Before Job");
    }

    public void afterJob(JobExecution jobExecution) {
        log.info("After Job");
    }

    @BeforeChunk
    public void beforeChunk(JobExecution jobExecution) {
        log.info("Before Chunk");
    }

    @AfterChunk
    public void afterChunk(JobExecution jobExecution) {
        log.info("After Chunk");
    }
}
