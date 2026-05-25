# C Language Practice System

A simple full-stack C programming exercise system built with Vue and Spring Boot.

## Features

- Browse C language programming problems
- View examples and test case count
- Write and submit C code in the browser
- Compile code with local `gcc`
- Run sample test cases and return pass/fail details
- Keep recent submission history in the frontend

## Project Structure

```text
backend/   Spring Boot REST API and local C judge
frontend/  Vue single page application
```

## Requirements

- JDK 8+
- Maven 3.6+
- Node.js 14+
- npm 6+
- gcc available in `PATH`

## Run Backend

```bash
cd backend
mvn spring-boot:run
```

The API runs at `http://localhost:8080`.

## Run Frontend

```bash
cd frontend
npm install
npm run dev
```

The app runs at `http://localhost:5173`.

## API

- `GET /api/problems` - list problems
- `GET /api/problems/{id}` - get problem detail
- `POST /api/submissions` - submit C code

Example submission body:

```json
{
  "problemId": 1,
  "code": "#include <stdio.h>\nint main(){int a,b;scanf(\"%d%d\",&a,&b);printf(\"%d\\n\",a+b);return 0;}"
}
```

## Safety Note

This project is intended for local learning and course demos. It executes submitted C programs on the local machine. For production or public use, replace `JudgeService` with a sandboxed runner using containers, strict resource limits, filesystem isolation, and user authentication.
