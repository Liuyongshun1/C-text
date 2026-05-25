package com.example.cjudge.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SubmissionRequest {
    @NotNull
    private Long problemId;

    @NotBlank
    private String code;

    public Long getProblemId() {
        return problemId;
    }

    public void setProblemId(Long problemId) {
        this.problemId = problemId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
