package com.example.cjudge.service;

import com.example.cjudge.model.Problem;
import com.example.cjudge.model.ProblemSummary;
import com.example.cjudge.model.TestCase;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProblemService {
    private final Map<Long, Problem> problems = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        add(new Problem(
                1L,
                "A + B 问题",
                "入门",
                "输入两个整数 a 和 b，输出它们的和。",
                "一行包含两个整数 a 和 b。",
                "输出一个整数，表示 a + b 的结果。",
                "1 2",
                "3",
                Arrays.asList(
                        new TestCase("1 2\n", "3\n"),
                        new TestCase("10 -3\n", "7\n"),
                        new TestCase("100 200\n", "300\n")
                )
        ));

        add(new Problem(
                2L,
                "判断奇偶",
                "入门",
                "输入一个整数 n，如果 n 是偶数输出 EVEN，否则输出 ODD。",
                "一行包含一个整数 n。",
                "输出 EVEN 或 ODD。",
                "7",
                "ODD",
                Arrays.asList(
                        new TestCase("7\n", "ODD\n"),
                        new TestCase("8\n", "EVEN\n"),
                        new TestCase("0\n", "EVEN\n")
                )
        ));

        add(new Problem(
                3L,
                "数组最大值",
                "基础",
                "输入 n 个整数，输出其中的最大值。",
                "第一行是整数 n，第二行包含 n 个整数。",
                "输出一个整数，表示最大值。",
                "5\n1 9 3 7 2",
                "9",
                Arrays.asList(
                        new TestCase("5\n1 9 3 7 2\n", "9\n"),
                        new TestCase("3\n-5 -2 -9\n", "-2\n"),
                        new TestCase("1\n42\n", "42\n")
                )
        ));
    }

    public List<ProblemSummary> listProblems() {
        List<ProblemSummary> summaries = new ArrayList<>();
        for (Problem problem : problems.values()) {
            summaries.add(new ProblemSummary(
                    problem.getId(),
                    problem.getTitle(),
                    problem.getDifficulty(),
                    problem.getTestCases().size()
            ));
        }
        return summaries;
    }

    public Problem findById(Long id) {
        return problems.get(id);
    }

    private void add(Problem problem) {
        problems.put(problem.getId(), problem);
    }
}
