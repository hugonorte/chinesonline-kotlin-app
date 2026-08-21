# Technical Decisions

This document outlines the rationale behind the primary technologies chosen for the ChinesOnline app.

## 1. Android Nativo (Kotlin) & Jetpack Compose
**Why**: Jetpack Compose permite construir interfaces dinâmicas e de alta performance de forma declarativa usando puramente Kotlin.
**Constraint**: Toda a UI deve ser feita em Compose (sem arquivos XML).

## 2. Abordagem de Product Flavors (Lite vs Premium)
**Why**: Manter um único repositório reduz a duplicação de código. Gerar dois APKs/AABs isola o código da versão Premium para fins de segurança, garantindo que o usuário Lite não tenha o código de configurações avançadas no celular.
**Constraint**: É estritamente proibido o uso de `if (isPremium)`. As diferenças devem ser resolvidas via interfaces implementadas nos *source sets* correspondentes e resolvidas via Injeção de Dependências.

## 3. Google Firebase & Backend Go
**Why**: Firebase Auth cuida do ciclo de vida complexo do usuário. O Backend em Go faz a validação rigorosa (server-side validation) das respostas do jogo e evita trapaças de rede.

## 4. State Management (ViewModel + StateFlow)
**Why**: Passar o estado manualmente em um jogo com timers e pontuações é passível de erros. Usar a arquitetura MVVM do Android com `StateFlow` garante que a UI em Compose apenas reaja ao estado atual (`GameState.Playing`, `GameState.LevelComplete`).
