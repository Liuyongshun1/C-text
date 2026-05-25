package com.example.cjudge.model;

import java.util.List;

public class SubmissionResult {
    private String status;
    private int passedCount;
    private int totalCount;
    private long elapsedMillis;
    private String compileMessage;
    private List<TestCaseResult> cases;

    public SubmissionResult() {
    }

    public SubmissionResult(String status, int passedCount, int totalCount,
                            long elapsedMillis, String compileMessage, List<TestCaseResult> cases) {
        this.status = status;
        this.passedCount = passedCount;
        this.totalCount = totalCount;
        this.elapsedMillis = elapsedMillis;
        this.compileMessage = compileMessage;
        this.cases = cases;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPassedCount() {
        return passedCount;
    }

    public void setPassedCount(int passedCount) {
        this.passedCount = passedCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public long getElapsedMillis() {
        return elapsedMillis;
    }

    public void setElapsedMillis(long elapsedMillis) {
        this.elapsedMillis = elapsedMillis;
    }

    public String getCompileMessage() {
        return compileMessage;
    }

    public void setCompileMessage(String compileMessage) {
        this.compileMessage = compileMessage;
    }

    public List<TestCaseResult> getCases() {
        return cases;
    }

    public void setCases(List<TestCaseResult> cases) {
        this.cases = cases;
    }
}
