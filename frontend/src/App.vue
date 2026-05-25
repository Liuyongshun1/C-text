<script setup>
import { computed, onMounted, ref } from 'vue'

const apiBase = 'http://localhost:8080/api'

const problems = ref([])
const currentProblem = ref(null)
const selectedId = ref(null)
const code = ref(`#include <stdio.h>

int main() {
    int a, b;
    scanf("%d%d", &a, &b);
    printf("%d\\n", a + b);
    return 0;
}
`)
const result = ref(null)
const loading = ref(false)
const error = ref('')
const history = ref([])

const statusLabel = computed(() => {
  if (!result.value) return ''
  const labels = {
    ACCEPTED: '通过',
    WRONG_ANSWER: '未通过',
    COMPILE_ERROR: '编译错误',
    COMPILE_TIMEOUT: '编译超时',
    SYSTEM_ERROR: '系统错误'
  }
  return labels[result.value.status] || result.value.status
})

const passedRate = computed(() => {
  if (!result.value || result.value.totalCount === 0) return 0
  return Math.round((result.value.passedCount / result.value.totalCount) * 100)
})

onMounted(async () => {
  await loadProblems()
})

async function loadProblems() {
  error.value = ''
  try {
    const response = await fetch(`${apiBase}/problems`)
    problems.value = await ensureOk(response)
    if (problems.value.length > 0) {
      await selectProblem(problems.value[0].id)
    }
  } catch (err) {
    error.value = `题库加载失败：${err.message}`
  }
}

async function selectProblem(id) {
  selectedId.value = id
  result.value = null
  error.value = ''
  try {
    const response = await fetch(`${apiBase}/problems/${id}`)
    currentProblem.value = await ensureOk(response)
    code.value = templateFor(id)
  } catch (err) {
    error.value = `题目加载失败：${err.message}`
  }
}

async function submitCode() {
  if (!currentProblem.value || loading.value) return
  loading.value = true
  result.value = null
  error.value = ''
  try {
    const response = await fetch(`${apiBase}/submissions`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        problemId: currentProblem.value.id,
        code: code.value
      })
    })
    result.value = await ensureOk(response)
    history.value.unshift({
      id: Date.now(),
      title: currentProblem.value.title,
      status: result.value.status,
      passed: `${result.value.passedCount}/${result.value.totalCount}`,
      time: new Date().toLocaleTimeString()
    })
    history.value = history.value.slice(0, 6)
  } catch (err) {
    error.value = `提交失败：${err.message}`
  } finally {
    loading.value = false
  }
}

async function ensureOk(response) {
  if (!response.ok) {
    const text = await response.text()
    throw new Error(text || `HTTP ${response.status}`)
  }
  return response.json()
}

function templateFor(id) {
  const templates = {
    1: `#include <stdio.h>

int main() {
    int a, b;
    scanf("%d%d", &a, &b);
    printf("%d\\n", a + b);
    return 0;
}
`,
    2: `#include <stdio.h>

int main() {
    int n;
    scanf("%d", &n);
    if (n % 2 == 0) {
        printf("EVEN\\n");
    } else {
        printf("ODD\\n");
    }
    return 0;
}
`,
    3: `#include <stdio.h>

int main() {
    int n;
    scanf("%d", &n);
    int maxValue;
    scanf("%d", &maxValue);
    for (int i = 1; i < n; i++) {
        int value;
        scanf("%d", &value);
        if (value > maxValue) {
            maxValue = value;
        }
    }
    printf("%d\\n", maxValue);
    return 0;
}
`
  }
  return templates[id] || ''
}
</script>

<template>
  <main class="shell">
    <aside class="sidebar">
      <div class="brand">
        <div class="mark">C</div>
        <div>
          <h1>C 语言做题系统</h1>
          <p>题库练习与本地判题</p>
        </div>
      </div>

      <section class="problem-list">
        <button
          v-for="problem in problems"
          :key="problem.id"
          class="problem-item"
          :class="{ active: problem.id === selectedId }"
          @click="selectProblem(problem.id)"
        >
          <span class="problem-title">{{ problem.title }}</span>
          <span class="problem-meta">{{ problem.difficulty }} · {{ problem.testCaseCount }} 组用例</span>
        </button>
      </section>

      <section class="history" v-if="history.length">
        <h2>最近提交</h2>
        <div v-for="item in history" :key="item.id" class="history-row">
          <span>{{ item.title }}</span>
          <strong :class="{ accepted: item.status === 'ACCEPTED' }">{{ item.passed }}</strong>
          <small>{{ item.time }}</small>
        </div>
      </section>
    </aside>

    <section class="workspace" v-if="currentProblem">
      <header class="topbar">
        <div>
          <p class="eyebrow">Problem #{{ currentProblem.id }}</p>
          <h2>{{ currentProblem.title }}</h2>
        </div>
        <button class="submit-button" :disabled="loading" @click="submitCode">
          {{ loading ? '判题中...' : '提交代码' }}
        </button>
      </header>

      <p class="error" v-if="error">{{ error }}</p>

      <div class="content-grid">
        <article class="statement">
          <section>
            <h3>题目描述</h3>
            <p>{{ currentProblem.description }}</p>
          </section>
          <section>
            <h3>输入说明</h3>
            <p>{{ currentProblem.inputDescription }}</p>
          </section>
          <section>
            <h3>输出说明</h3>
            <p>{{ currentProblem.outputDescription }}</p>
          </section>
          <div class="sample-grid">
            <section>
              <h3>样例输入</h3>
              <pre>{{ currentProblem.sampleInput }}</pre>
            </section>
            <section>
              <h3>样例输出</h3>
              <pre>{{ currentProblem.sampleOutput }}</pre>
            </section>
          </div>
        </article>

        <section class="editor-pane">
          <div class="editor-head">
            <h3>C 代码</h3>
            <span>gcc · C11</span>
          </div>
          <textarea v-model="code" spellcheck="false" />
        </section>
      </div>

      <section class="result" v-if="result">
        <div class="result-summary">
          <div>
            <p class="eyebrow">Judge Result</p>
            <h3 :class="{ accepted: result.status === 'ACCEPTED' }">{{ statusLabel }}</h3>
          </div>
          <div class="score">
            <strong>{{ result.passedCount }}/{{ result.totalCount }}</strong>
            <span>{{ passedRate }}% · {{ result.elapsedMillis }}ms</span>
          </div>
        </div>

        <pre class="compile-message" v-if="result.compileMessage">{{ result.compileMessage }}</pre>

        <div class="case-list">
          <article v-for="item in result.cases" :key="item.index" class="case-row">
            <div class="case-title">
              <strong>用例 {{ item.index }}</strong>
              <span :class="{ accepted: item.passed }">{{ item.message }}</span>
            </div>
            <div class="case-io">
              <pre>输入:
{{ item.input }}</pre>
              <pre>期望:
{{ item.expectedOutput }}</pre>
              <pre>实际:
{{ item.actualOutput }}</pre>
            </div>
          </article>
        </div>
      </section>
    </section>
  </main>
</template>
