package com.community.batch.jobs;

import com.community.batch.domain.User;

import org.springframework.batch.item.ItemProcessor;

import java.util.List;

/**
 * Created by KimYJ on 2018-03-09.
 */
public class InactiveUserProcessor implements ItemProcessor<List<User>, List<User>> {
    @Override
    public List<User> process(List<User> item) throws Exception {
        return item;
    }
}
