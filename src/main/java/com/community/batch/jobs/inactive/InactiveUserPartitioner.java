package com.community.batch.jobs.inactive;

import com.community.batch.domain.enums.Grade;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.util.HashMap;
import java.util.Map;

public class InactiveUserPartitioner implements Partitioner {

    private static final String GRADE = "grade";

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> map = new HashMap<>(gridSize);
        Grade[] grades = Grade.values();
        for (int i = 0, length = grades.length; i < length; i++) {
            ExecutionContext context = new ExecutionContext();
            context.putString(GRADE, grades[i].name());
            map.put("InactiveUserTask" + i, context);
        }
        return map;
    }
}
