package com.example.cjudge.model;

public class ProblemSummary {
    private Long id;
    private String title;
    private String difficulty;
    private int testCaseCount;

    public ProblemSummary() {
    }

    public ProblemSummary(Long id, String title, String difficulty, int testCaseCount) {
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.testCaseCount = testCaseCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public int getTestCaseCount() {
        return testCaseCount;
    }

    public void setTestCaseCount(int testCaseCount) {
        this.testCaseCount = testCaseCount;
    }
}
