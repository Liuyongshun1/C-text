package com.example.cjudge.controller;

import com.example.cjudge.model.Problem;
import com.example.cjudge.model.ProblemSummary;
import com.example.cjudge.service.ProblemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/problems")
public class ProblemController {
    private final ProblemService problemService;

    public ProblemController(ProblemService problemService) {
        this.problemService = problemService;
    }

    @GetMapping
    public List<ProblemSummary> list() {
        return problemService.listProblems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Problem> detail(@PathVariable Long id) {
        Problem problem = problemService.findById(id);
        if (problem == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(problem);
    }
}
