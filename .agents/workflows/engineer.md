---
description: Orchestrate the implementation phase of the development cycle for Mobile App tasks using TDD and Kotlin.
---

# Workflow: Engineer Implementation

When acting as the **Full-Stack Engineer**, follow this sequence to translate the approved **Technical Specification** into production-ready Android Native code.

### 1. Analysis and Setup
- **Read Specification**: Open `Technical_Specification.md` and identify all required views, components, and data structures.
- **Review Conventions**: Re-read `.agents/rules/kotlin-conventions.md` (or equivalent rules) to ensure project-specific standards (Jetpack Compose, MVVM, Clean Architecture) are applied.
- **Security Check**: Review `.agents/rules/security.md` to prevent accidental inclusion of secrets or unsafe data handling.
- **Branching**: Ensure you are on the `dev` branch or creating a feature branch rooted in `dev` before starting work. All PRs must target `dev`.
- **Type safety**: Verifique sempre o uso correto de interfaces e tipos no Kotlin, garantindo Null Safety.

### 2. Test-Driven Development (TDD) - OBRIGATÓRIO
- **Red Phase (Testes Primeiro):** Antes de escrever QUALQUER código de tela ou regra de negócio, você deve obrigatoriamente criar o arquivo de teste respectivo (Ex: `QuizViewModelTest.kt` ou testes de UI com Compose Test Rule).
- **Rodar os Testes:** Execute os testes localmente via terminal (ex: `./gradlew test`) para provar que eles falham (estado Red).
- **Green Phase (Implementação):** Só após a falha confirmada, inicie a etapa de "Physical Implementation" escrevendo apenas o código necessário para fazer o teste passar.
- **Refactor:** Limpe o código garantindo que os testes continuem passando.
- **Regra de Ouro:** É terminantemente proibido criar arquivos de UI ou ViewModels sem criar e rodar o arquivo de teste antes.

### 3. Physical Implementation
- **Execute generate_code Skill**: Trigger the `generate_code.md` skill to scaffold and populate the requested features.
- **Component Drafting**:
    - Use **PascalCase** para novos componentes Compose (e.g., `BookForm.kt`).
    - Desenvolva as lógicas utilizando ViewModels e `StateFlow`.
    - Separe o estado da UI da emissão de eventos (MVI/MVVM pattern).
    - Certifique-se de usar Strings extraídas em `strings.xml` para Internacionalização.
- **Styling**:
    - Utilize os temas definidos no Material Design 3 (`MaterialTheme.colorScheme`, `MaterialTheme.typography`).
    - Defina cores específicas no arquivo `Color.kt` e evite hardcoding na UI.

### 4. Self-Audit and Handover
- **Syntax & Build Check**: Run `./gradlew assembleDebug` para garantir que não existam erros de compilação Kotlin.
- **Clean Code Review**: Remove any debugging logs or commented-out code.
- **Handover to QA**: Once implementation is complete, signal readiness for the `audit_code.md` skill.