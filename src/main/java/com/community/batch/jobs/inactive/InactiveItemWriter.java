package com.community.batch.jobs.inactive;

import com.community.batch.domain.User;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Created by KimYJ on 2018-04-02.
 */
public class InactiveItemWriter implements ItemWriter<User> {

    private UserRepository userRepository;

    public InactiveItemWriter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void write(List<? extends User> users) throws Exception {
        userRepository.saveAll(users);
    }
}
