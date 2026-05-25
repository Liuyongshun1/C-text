package com.example.cjudge.service;

import com.example.cjudge.model.Problem;
import com.example.cjudge.model.SubmissionResult;
import com.example.cjudge.model.TestCase;
import com.example.cjudge.model.TestCaseResult;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class JudgeService {
    private static final long COMPILE_TIMEOUT_SECONDS = 8;
    private static final long RUN_TIMEOUT_SECONDS = 3;

    public SubmissionResult judge(Problem problem, String code) {
        long start = System.currentTimeMillis();
        Path workDir = null;
        try {
            workDir = Files.createTempDirectory("c-judge-");
            Path sourcePath = workDir.resolve("main.c");
            Path exePath = workDir.resolve(isWindows() ? "main.exe" : "main");
            Files.write(sourcePath, code.getBytes(StandardCharsets.UTF_8));

            ProcessResult compileResult = runProcess(
                    new String[]{"gcc", sourcePath.toString(), "-O2", "-std=c11", "-o", exePath.toString()},
                    workDir.toFile(),
                    "",
                    COMPILE_TIMEOUT_SECONDS
            );

            if (compileResult.timeout) {
                return new SubmissionResult("COMPILE_TIMEOUT", 0, problem.getTestCases().size(),
                        elapsedSince(start), "编译超时", new ArrayList<TestCaseResult>());
            }
            if (compileResult.exitCode != 0) {
                return new SubmissionResult("COMPILE_ERROR", 0, problem.getTestCases().size(),
                        elapsedSince(start), compileResult.output, new ArrayList<TestCaseResult>());
            }

            List<TestCaseResult> caseResults = new ArrayList<>();
            int passed = 0;
            int index = 1;
            for (TestCase testCase : problem.getTestCases()) {
                ProcessResult runResult = runProcess(
                        new String[]{exePath.toString()},
                        workDir.toFile(),
                        testCase.getInput(),
                        RUN_TIMEOUT_SECONDS
                );

                String actual = normalize(runResult.output);
                String expected = normalize(testCase.getExpectedOutput());
                boolean ok = !runResult.timeout && runResult.exitCode == 0 && expected.equals(actual);
                if (ok) {
                    passed++;
                }

                String message = ok ? "通过" : buildFailureMessage(runResult, expected, actual);
                caseResults.add(new TestCaseResult(
                        index++,
                        ok,
                        testCase.getInput(),
                        testCase.getExpectedOutput(),
                        runResult.output,
                        message
                ));
            }

            String status = passed == problem.getTestCases().size() ? "ACCEPTED" : "WRONG_ANSWER";
            return new SubmissionResult(status, passed, problem.getTestCases().size(),
                    elapsedSince(start), compileResult.output, caseResults);
        } catch (IOException ex) {
            return new SubmissionResult("SYSTEM_ERROR", 0, problem.getTestCases().size(),
                    elapsedSince(start), ex.getMessage(), new ArrayList<TestCaseResult>());
        } finally {
            if (workDir != null) {
                deleteQuietly(workDir.toFile());
            }
        }
    }

    private ProcessResult runProcess(String[] command, File workDir, String input, long timeoutSeconds) throws IOException {
        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(workDir);
        builder.redirectErrorStream(true);
        Process process = builder.start();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8))) {
            writer.write(input == null ? "" : input);
        }

        boolean finished;
        try {
            finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            process.destroyForcibly();
            return new ProcessResult(-1, "执行被中断", true);
        }

        if (!finished) {
            process.destroyForcibly();
            String output = readAll(process.getInputStream());
            return new ProcessResult(-1, output, true);
        }
        String output = readAll(process.getInputStream());
        return new ProcessResult(process.exitValue(), output, false);
    }

    private String readAll(InputStream stream) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        }
        return builder.toString();
    }

    private String normalize(String value) {
        return value == null ? "" : value.replace("\r\n", "\n").trim();
    }

    private String buildFailureMessage(ProcessResult result, String expected, String actual) {
        if (result.timeout) {
            return "运行超时";
        }
        if (result.exitCode != 0) {
            return "运行错误，退出码 " + result.exitCode;
        }
        return "答案错误：期望 " + expected + "，实际 " + actual;
    }

    private long elapsedSince(long start) {
        return System.currentTimeMillis() - start;
    }

    private boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    private void deleteQuietly(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteQuietly(child);
                }
            }
        }
        file.delete();
    }

    private static class ProcessResult {
        private final int exitCode;
        private final String output;
        private final boolean timeout;

        private ProcessResult(int exitCode, String output, boolean timeout) {
            this.exitCode = exitCode;
            this.output = output;
            this.timeout = timeout;
        }
    }
}
