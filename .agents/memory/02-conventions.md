# Coding Conventions - Android, Kotlin & Compose

## 1. Naming Conventions
- **Classes, Interfaces, Enums**: `PascalCase`
- **Variables, Functions, Methods**: `camelCase`
- **Packages and Directories**: `lowercase` sem underscores
- **Constants**: `UPPER_SNAKE_CASE`
- **Compose UI Functions**: `PascalCase` (e.g., `@Composable fun QuizScreen()`)

## 2. Kotlin & Compose Patterns
- **State Management**: Utilize `StateFlow` dentro de `ViewModels`. Mantenha as funções Composable sem estado (stateless) o máximo possível.
- **Null Safety**: Tire proveito do sistema de null-safety do Kotlin. Evite o uso de `!!` (not-null assertion operator) a menos que absolutamente necessário.
- **Coroutines**: Utilize `viewModelScope` ou `lifecycleScope` para chamadas assíncronas (Network/DB).

## 3. Product Flavors e SOLID (MANDATÓRIO)
- **Proibido if/else de Flavors**: Nunca faça a checagem de qual versão o usuário está rodando (`lite` ou `premium`) usando `if/else` no código principal.
- **Injeção e Polimorfismo**: Comportamentos diferentes devem ser abstraídos em interfaces no `src/main` e injetados via framework de injeção de dependências (Hilt/Koin), com as implementações concretas residindo unicamente em `src/lite` ou `src/premium`.

## 3. Firebase Interactions
- **Data Models**: Use `fromJson` and `toJson` methods for serialization/deserialization of Firestore documents.
- **Collections**: Hardcode collection names as constants in a dedicated `FirebaseConstants` file to avoid typos.

## 4. UI/UX Rules
- **Themes**: Do not hardcode colors or text styles in widgets. Use `Theme.of(context)` to ensure consistency.
- **Responsive**: Design should be flexible, accommodating different phone screen sizes.
- **Localization**: All user-facing strings must use the `intl` package or Flutter's localizations. No hardcoded strings in the UI.

## 5. Testing
- Use `flutter test` for unit testing logic and state controllers.
- Use `flutter test --machine` for widget testing key UI components.
