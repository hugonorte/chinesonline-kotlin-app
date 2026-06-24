---
description: Orchestrate the implementation phase of the development cycle for Mobile App tasks using TDD and Kotlin/Jetpack Compose.
---

# Workflow: Engineer Implementation

When acting as the **Android Engineer**, follow this sequence to translate the approved **Technical Specification** into production-ready Android Native code using Kotlin and Jetpack Compose.

### 1. Analysis and Setup
- **Read Specification**: Open `Technical_Specification.md` and identify all required views, components, and data structures.
- **Review Conventions**: Re-read `.agents/rules/kotlin-conventions.md` (or equivalent rules) to ensure project-specific standards (Jetpack Compose, MVVM, Clean Architecture) are applied.
- **Security Check**: Review `.agents/rules/security.md` to prevent accidental inclusion of secrets or unsafe data handling.
- **Branching**: Ensure you are on the `dev` branch or creating a feature branch rooted in `dev` before starting work. All PRs must target `dev`.
- **Type safety**: Verifique sempre o uso correto de interfaces e tipos no Kotlin, garantindo Null Safety.

### 2. Test-Driven Development (TDD) - OBRIGATÓRIO E ITERATIVO
O desenvolvimento deve ser feito em ciclos curtos e granulares. **NUNCA escreva o teste e a implementação na mesma iteração ou resposta.**

- **Red Phase (Testes Primeiro):** 
  - Antes de escrever QUALQUER código de tela ou regra de negócio, crie apenas o arquivo de teste respectivo.
  - Para lógicas (ViewModels/UseCases), use JUnit4/JUnit5, MockK e Coroutines Test (ex: `runTest` e `UnconfinedTestDispatcher`).
  - Para telas, use `ComposeContentTestRule`. Determine se usará Robolectric (em `app/src/test/` para testes rápidos na JVM) ou testes instrumentados (em `app/src/androidTest/`).
- **Pausa Obrigatória e Execução:** Execute os testes no terminal correspondente:
  - JVM Local / Robolectric: `./gradlew test`
  - Instrumentados: `./gradlew connectedAndroidTest`
  - **HARD STOP:** Pare a execução de uso de ferramentas. Aguarde a saída do terminal provando que o código compila mas o teste falha (por ausência de código ou falha de asserção). Analise o log de erro e SÓ ENTÃO prossiga para a implementação.
- **Green Phase (Implementação):** Inicie a "Physical Implementation" apenas para o componente sendo testado, escrevendo estritamente o código necessário para fazer o teste passar. 

### 3. Physical Implementation (Iterative Scaffold)
- **Execute generate_code Skill**: Trigger the `generate_code.md` skill para gerar o código em pequenos incrementos (um componente por vez) para satisfazer o teste que está falhando.
- **Component Drafting**:
    - Use **PascalCase** para novos componentes Compose (e.g., `QuizScreen.kt`).
    - Desenvolva as lógicas utilizando ViewModels e `StateFlow`.
    - Separe o estado da UI da emissão de eventos (Padrão Unidirectional Data Flow).
- **Styling**:
    - Utilize os temas definidos no Material Design 3 (`MaterialTheme.colorScheme`, `MaterialTheme.typography`).
    - Defina cores específicas em arquivos de tema e evite hardcoding na UI.

### 4. Self-Audit and Handover
- **Syntax & Build Check**: Run `./gradlew assembleDebug` ou repita o comando de teste para garantir que não existam erros de compilação e que a suíte voltou ao estado Verde.
- **Clean Code Review**: Remove any debugging logs or commented-out code.
- **Handover to QA**: Once implementation is complete, signal readiness for the `audit_code.md` skill.