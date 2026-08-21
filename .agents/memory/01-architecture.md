# Project Architecture - ChinesOnline

## 1. Overview
This project is a mobile application built with **Flutter** and powered by **Google Firebase**. It functions as an interactive quiz game for learning Chinese characters and words.

## 2. Core Technologies
- **Mobile App**: Android Nativo (Kotlin)
- **UI Toolkit**: Jetpack Compose
- **Backend/BaaS**: API em Go + Firebase (Auth, Analytics, App Check)
- **State Management**: ViewModel + StateFlow
- **Roteamento**: Jetpack Navigation Compose

## 3. Directory Structure
We follow a **Feature-First Architecture** to keep the codebase modular and scalable.

```
app/
├── src/
│   ├── main/                 # Código comum a ambas as versões (Lite e Premium)
│   │   ├── java/com/example/chinesonline/
│   │   │   ├── core/         # Componentes globais, temas (Compose), utils
│   │   │   └── features/     # Módulos principais (auth, quiz, profile)
│   ├── lite/                 # Assets, configs e injeções exclusivas da versão Lite
│   └── premium/              # Telas avançadas e lógicas exclusivas da versão Premium
```

## 4. Product Flavors & Source Sets Strategy
Para gerar duas versões do app a partir do mesmo repositório:
- **Flavors**: `lite` e `premium` configurados no `build.gradle.kts`.
- **Constraint de Arquitetura**: NUNCA utilize if/else (`if (isPremium)`) no código do `src/main` para controlar funcionalidades. Utilize princípios **SOLID** (Polimorfismo, Inversão de Dependência) com interfaces no `main` sendo implementadas diferentemente nos source sets `lite` e `premium`. Isso evita o vazamento de código pago no APK gratuito.

## 4. Key Application Layers (Inside Features)
Each feature directory contains:
- `presentation/`: Widgets, Pages, and ViewModels/StateNotifier.
- `domain/`: Business logic, Entities, Models.
- `data/`: Repositories and APIs.

## 5. Domain Modules
### 5.1 Quiz Engine
- Manages the difficulty progression (Levels 1 to 8).
- Switches UI from Multiple Choice (Lv 1-2) to Text Input (Lv 3-8).
- Calculates points locally for feedback, but relies on Server validation.

### 5.2 User & Scoring
- Users are authenticated via Firebase Auth.
- Communicates with Go API to validate scores.
