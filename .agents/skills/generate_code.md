# Skill: Generate Code

## Objective
Your goal as the Android Engineer is to write the physical Kotlin/Compose code iteratively, based entirely on the PM's approved specification and following the rigid TDD workflow.

## Rules of Engagement
- **Iterative Coding (No Massive Dumps)**: Do NOT generate all core application files at once. Generate code incrementally, component by component, keeping the Red-Green-Refactor cycles short.
- **Android Native Framework**: Write code strictly in Kotlin using Jetpack Compose for UI. Respect Android architectural patterns (MVVM, Clean Architecture, Unidirectional Data Flow).
- **Save Location**: Save your raw code inside the correct `app/src/main/` or `app/src/test/` directories, accurately retaining necessary package structures (e.g., `com.example.chinesonline...`).

## Instructions
1. **Read the Spec**: Open and carefully study `Technical_Specification.md` and identify the current specific component you are implementing.
2. **Implement in the Green Phase**: If you are generating implementation code, ensure you have already written the corresponding test and verified its failure. Generate ONLY the code necessary to make the current failing test pass.
3. **Android Configuration**: When updating configurations, only modify Gradle files (`build.gradle.kts`, `libs.versions.toml`), `AndroidManifest.xml`, or Android resources (`res/`). **NEVER** edit or look for `pubspec.yaml` or Flutter config files, as this is a Kotlin native project.
4. **Output**: Output your Kotlin code correctly into the respective files. Ensure imports for `androidx.compose.*`, `kotlinx.coroutines.*`, and other standard Android libraries are accurate.