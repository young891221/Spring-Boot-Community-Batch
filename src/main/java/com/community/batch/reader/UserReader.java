package com.community.batch.reader;

import com.community.batch.domain.User;
import com.community.batch.repository.UserRepository;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by KimYJ on 2018-03-07.
 */
@Component
public class UserReader implements ItemReader<List<User>> {
    private UserRepository userRepository;

    public UserReader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return userRepository.findAll();
    }
}
