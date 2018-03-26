package com.community.batch;

import com.community.batch.domain.User;
import com.community.batch.domain.enums.UserStatus;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@SpringBootApplication
@EnableBatchProcessing
public class BatchApplication {
	private static final long MIN_DAY = LocalDate.of(2015, 3, 1).toEpochDay();
	private static final long MAX_DAY = LocalDate.of(2018, 3, 1).toEpochDay();

	public static void main(String[] args) {
		SpringApplication.run(BatchApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner(UserRepository userRepository) {
		return (args) -> {
			List<User> users = new ArrayList<>();

			IntStream.rangeClosed(1, 100).forEach(index ->
					users.add(User.builder()
							.name("user" + index)
							.password("test" + index)
							.email("test@gmail.com")
							.status(UserStatus.ACTIVE)
							.createdDate(LocalDateTime.of(2015, 3, 1, 0, 0))
							.updatedDate(makeRandomDateTime())
							.build()));

			userRepository.saveAll(users);
		};
	}

	private LocalDateTime makeRandomDateTime() {
		long randomDay = ThreadLocalRandom.current().nextLong(MIN_DAY, MAX_DAY);
		return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN);
	}
}
