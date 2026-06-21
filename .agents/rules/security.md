---
trigger: always_on
---

# Proteção de Informações Sensíveis (Mobile App)

Esta regra visa prevenir o vazamento de dados sensíveis para o repositório Git e garantir que segredos não sejam expostos indevidamente no bundle final enviado ao navegador.

## Regras de Segurança:

- **Não incluir Credenciais:** Nunca escreva senhas, tokens de API privados, segredos de clientes ou chaves de criptografia diretamente no código-fonte.
- **Uso de Variáveis de Ambiente e Runtime Config (Flutter):** 
    - Utilize o arquivo `.env` para armazenar configurações de ambiente local.
    - **Uso de pacotes seguros:** Em Flutter, use pacotes como `flutter_secure_storage` para armazenar tokens JWT ou dados sensíveis.
    - **Público vs Privado:** Não armazene chaves de API secretas (ex: chaves mestras de backend) no aplicativo cliente. Elas podem ser facilmente extraídas. O Flutter é um ambiente de cliente (frontend).
    - **Prefixos:** Em Flutter, o pacote `flutter_dotenv` pode ser usado para ler variáveis do `.env`, e os valores podem ser injetados em tempo de compilação usando `--dart-define`.
    - **Acesso:** Acesse suas variáveis via `String.fromEnvironment()` para chaves seguras passadas no build.
- **Verificação Proativa:** Antes de finalizar qualquer tarefa que envolva criação ou edição de arquivos de configuração, componentes de autenticação ou serviços de API, verifique se campos como `Password`, `Secret`, `Token` ou `Key` não possuem valores padrão expostos no código.
- **Sanitização de Logs e UI:** 
    - Garanta que `console.log` não capture informações sensíveis dos usuários (PII) ou tokens de autenticação em ambiente de produção.
    - Não exiba mensagens de erro detalhadas do backend (stack traces, erros de SQL) diretamente na interface do usuário.
- **Armazenamento Local:** Evite armazenar informações altamente sensíveis (como senhas em texto puro) em `localStorage` ou `sessionStorage`. Prefira o uso de cookies `HttpOnly` (gerenciados pelo backend) sempre que a arquitetura o permitir.
- **Dados de Teste:** Use apenas dados fictícios/mockados para testes e exemplos. Nunca use dados reais de usuários ou chaves de produção em arquivos de teste ou documentação.
