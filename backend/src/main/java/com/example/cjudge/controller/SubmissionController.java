package com.example.cjudge.controller;

import com.example.cjudge.model.Problem;
import com.example.cjudge.model.SubmissionRequest;
import com.example.cjudge.model.SubmissionResult;
import com.example.cjudge.service.JudgeService;
import com.example.cjudge.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final ProblemService problemService;
    private final JudgeService judgeService;

    public SubmissionController(ProblemService problemService, JudgeService judgeService) {
        this.problemService = problemService;
        this.judgeService = judgeService;
    }

    @PostMapping
    public ResponseEntity<SubmissionResult> submit(@Valid @RequestBody SubmissionRequest request) {
        Problem problem = problemService.findById(request.getProblemId());
        if (problem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(judgeService.judge(problem, request.getCode()));
    }
}
