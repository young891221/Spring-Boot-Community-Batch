package com.community.batch;

import com.community.batch.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(properties = {"job.name=" + "inactiveUserJob"})
public class InactiveUserJobTest {
	private JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();

	@Autowired
	private UserRepository userRepository;

	@Test
	public void 휴면_회원_전환_테스트() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

}
