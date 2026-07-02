# Política de Testes e TDD Obrigatório

Esta regra tem prioridade máxima durante as fases de implementação, refatoração ou correção de bugs, especialmente quando o agente atuar com o papel de `@engineer`.

## 1. Test-Driven Development (TDD) Estrito
- **Sem Brechas:** É **estritamente proibido** escrever código de produção (funcionalidades, novos arquivos, lógica de negócios) antes de criar ou atualizar os respectivos **Testes Unitários**.
- O ciclo do desenvolvimento deve seguir obrigatoriamente: `Red` (Criar o teste que falha) -> `Green` (Escrever a menor quantidade de código para passar no teste) -> `Refactor` (Limpar o código).

## 2. Cobertura de Código
- Toda nova classe, função, ViewModel, Repository ou UseCase deve ter testes unitários correspondentes mapeando os fluxos de sucesso (Happy Path) e cenários de erro/exceção.
- Se uma classe legada for refatorada, o agente deve garantir que os testes unitários dessa classe sejam atualizados ou criados caso não existam.

## 3. Critérios de Conclusão de Tarefa (Definition of Done)
Nenhuma tarefa ou issue pode ser declarada como "Pronta", "Concluída" ou apresentada em um `walkthrough` sem antes cumprir o seguinte checklist de qualidade:
1. **Testes Unitários:** Foram criados/atualizados e executam com sucesso.
2. **Testes de Integração:** Fluxos que conectam múltiplas camadas (ex: UI chamando ViewModel que chama Repository mockado) devem ser testados.
3. **Testes de Regressão:** O agente deve executar a suíte de testes do projeto para garantir que as alterações não quebraram outras partes do aplicativo.

## 4. Política Estrita de Testes E2E (Proteção de Cota do Firebase)
Para proteger a cota gratuita do Firebase e evitar consumos desnecessários na nuvem:
- **Testes E2E Locais (Simulados):** O agente deve priorizar testes simulados na JVM (Robolectric) e MockWebServer.
- **PROIBIÇÃO de E2E em Produção/Live (Caminho 1):** É **ESTRITAMENTE PROIBIDO** que qualquer agente execute testes E2E reais que batam diretamente nos servidores ao vivo (Live) do Firebase (ex: chamadas de rede não-mockadas via emulador/dispositivo).
- **Autorização Explícita:** Se houver necessidade absoluta de validar uma integração real de ponta a ponta na nuvem (Live Firebase), o agente **DEVE** pausar a execução e solicitar autorização explícita e clara ao USUÁRIO através do chat. O teste só pode prosseguir após o usuário responder com "Sim" ou "Autorizado". Nunca presuma autorização e nunca execute de forma automatizada.

## 5. Dependências e Mocks
- Isole componentes usando bibliotecas de Mocking (ex: `MockK` ou `Mockito` no Kotlin, `mockito` no Flutter/Dart).
- Se componentes possuírem forte acoplamento (ex: chamadas estáticas de SDKs de terceiros como `Firebase.getInstance()`), o código DEVE ser refatorado para injeção de dependência ou o teste deve utilizar mock estático adequado.
