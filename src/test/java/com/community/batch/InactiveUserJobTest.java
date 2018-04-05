package com.community.batch;

import com.community.batch.domain.User;
import com.community.batch.repository.UserRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import static com.community.batch.domain.enums.UserStatus.ACTIVE;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class InactiveUserJobTest {
	private static final long MIN_DAY = LocalDate.of(2015, 3, 1).toEpochDay();
	private static final long MAX_DAY = LocalDate.of(2018, 3, 1).toEpochDay();

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private UserRepository userRepository;

	@MockBean(name = "userRepository")
	private UserRepository mockUserRepository;

	/*@MockBean(name = "inactiveUserReader")
	private QueueItemReader<User> mockReader;*/

	@Test
	public void 휴면_회원_전환_테스트() throws Exception {
		List<User> users = new ArrayList<>();

		IntStream.rangeClosed(1, 100).forEach(index ->
				users.add(User.builder()
						.name("user" + index)
						.password("test" + index)
						.email("test@gmail.com")
						.status(ACTIVE)
						.createdDate(LocalDateTime.of(2015, 3, 1, 0, 0))
						.updatedDate(makeRandomDateTime())
						.build()));

		when(mockUserRepository.findByUpdatedDateBeforeAndStatusEquals(any(LocalDateTime.class), ACTIVE)).thenReturn(users);

		/*when(mockReader.read()).thenReturn(User.builder()
				.name("user")
				.password("test")
				.email("test@gmail.com")
				.status(UserStatus.ACTIVE)
				.createdDate(LocalDateTime.of(2015, 3, 1, 0, 0))
				.updatedDate(LocalDateTime.of(2015, 3, 1, 0, 0))
				.build(), null);*/

		JobExecution jobExecution = jobLauncherTestUtils.launchJob();

		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		assertEquals(100, userRepository.findAll().size());
		assertEquals(0, userRepository.findByUpdatedDateBeforeAndStatusEquals(LocalDateTime.now().minusYears(1), ACTIVE).size());
	}

	private LocalDateTime makeRandomDateTime() {
		long randomDay = ThreadLocalRandom.current().nextLong(MIN_DAY, MAX_DAY);
		return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN);
	}
}
