# Technical Specification - ChinesOnline (Mobile App)

## Executive Summary

Este documento serve como a "Fonte da Verdade" escrita pelo **@pm** para os agentes **@engineer** e **@qa**. O objetivo é construir o aplicativo móvel do **ChinesOnline** utilizando **Kotlin e Jetpack Compose**. O sistema utilizará uma arquitetura distribuída com um Backend em **Go (Google Cloud)** e um painel administrativo independente (já isolado). O foco central do app é entregar uma experiência gamificada ultrarrápida, de baixo custo em nuvem e **extremamente segura contra fraudes (Anti-Cheat)**.

> [!IMPORTANT]
> **Atenção @engineer (ANDROID VITALS É A PRIORIDADE MÁXIMA):** 
> Toda e qualquer decisão de implementação de código, arquitetura, design patterns e gerenciamento de estado no ecossistema Kotlin/Compose devem ter como objetivo número 1 garantir uma excelente pontuação no **Android Vitals**. O app deve possuir:
> 1. Taxa de Crash próxima a zero (uso forte de `try/catch` e Kotlin Coroutines Exception Handling).
> 2. Zero ANRs (Todo I/O pesado, parse de JSON e criptografia devem ser feitos via `Dispatchers.IO` ou `Dispatchers.Default`).
> 3. Rendering impecável a 60fps (sem Jank). Evite recomposições desnecessárias no Jetpack Compose, utilize `remember`, estados bem granulares (`StateFlow`) e perfilamento (Profile Mode).
> 4. Baixo consumo de rede e bateria (otimização extrema e uso inteligente de cache local nativo via Room).

---

## 1. Tech Stack & Infrastructure (App Mobile)

### 1.1 Framework Core
- **Plataforma**: Android Nativo.
- **Linguagem**: Kotlin.
- **UI Toolkit**: Jetpack Compose.
- **Gerenciamento de Estado**: `ViewModel` + `StateFlow` / `SharedFlow`.
- **Roteamento**: Jetpack Navigation Compose.

### 1.2 Integração com Backend e Serviços
- **Backend Principal**: API REST em Go (Golang) hospedada no Google Cloud Run.
- **Autenticação**: Firebase Authentication (Email/Senha, Google Sign-In). O app Android envia o JWT do Firebase para a API em Go autorizar as requisições.
- **Analytics & Segurança**: Google Analytics for Firebase e **Firebase App Check** (Play Integrity).
- **Network Client**: Retrofit + OkHttp.
- **Banco de Dados Local**: Room Database / DataStore.

---

## 2. Core Game Loop & Economia de Infraestrutura

Para garantir jogabilidade fluida (sem loading entre perguntas) e economia massiva de recursos no Cloud Run, o aplicativo deve adotar uma abordagem baseada em **Lotes (Batches) e Gameplay Local**.

### 2.1 O Fluxo de Lotes (Sessions)
1. **Início da Rodada**: O App (Kotlin) requisita `GET /api/v1/sessions/new?level=X`. O servidor retorna um lote com 10-20 ideogramas e os metadados de segurança (salts e hashes).
2. **Gameplay 100% Local**: O usuário joga toda a rodada off-line/local. O app valida os acertos/erros usando os hashes (veja seção de Anti-Cheat) para dar feedback imediato de UI (verde/vermelho) sem fazer nenhuma requisição de rede.
3. **Fim da Rodada**: O App compila as respostas brutas dadas pelo usuário e envia para `POST /api/v1/sessions/{id}/submit`.

---

## 3. Segurança e Anti-Cheat (Crucial)

O aplicativo é o cliente e **nunca deve ser confiado**. O pacote de segurança se divide em 5 pilares:

### 3.1 Feedback Imediato Seguro (Hashing com Salt por Questão)
Para que a tela brilhe verde ou vermelho na hora, o app precisa validar a resposta, mas **não pode** ter a resposta certa em texto plano na RAM (para evitar leitores de memória).
- **A Estratégia**: A API envia no lote um `salt` dinâmico e o `correct_hash` (ex: `SHA256(resposta_certa + salt)`). 
- **No Kotlin**: O usuário escolhe/digita a resposta, o app concatena com o `salt` e faz o Hash. Se bater com o `correct_hash`, a UI reage positivamente. Todo esse processo criptográfico DEVE ocorrer fora da Main Thread (no `Dispatchers.Default`) para evitar **ANRs (Android Vitals)**.

### 3.2 Validação Server-Side Obrigatória
- O aplicativo Android **jamais** calcula a própria pontuação ou envia ao backend algo como `{"pontos": 100}`.
- O payload de fechamento de sessão (`POST /submit`) contém apenas a ID da questão e a string exata que o usuário respondeu. O servidor Go irá "corrigir a prova", calcular os pontos de forma imutável e atualizar o banco de dados.

### 3.3 Proteção contra Time-Spoofing (Bots rápidos)
- O backend salvará a hora em que entregou o lote e a hora em que recebeu o `POST /submit`. 
- **O App não precisa fazer nada especial aqui**, apenas enviar as métricas de tempo gasto em cada questão. O servidor anulará rodadas onde 10 questões difíceis forem resolvidas em menos de 1 segundo (comportamento impossível para humanos).

### 3.4 Proteção contra Engenharia Reversa (App Check)
- É obrigatório configurar a dependência `firebase-appcheck-playintegrity` no Android. O token gerado deve ser incluído no header das chamadas (Retrofit Interceptor) para a API Go, garantindo que a requisição veio do app original compilado pela Play Store, e não de um script malicioso.

> [!IMPORTANT]
> **Atenção @engineer para Implementação do App Check:**
> 1. Adicione a dependência no `build.gradle.kts`.
> 2. No `Application` class, inicialize o App Check logo após o Firebase:
>    ```kotlin
>    Firebase.initialize(context)
>    Firebase.appCheck.installAppCheckProviderFactory(
>        PlayIntegrityAppCheckProviderFactory.getInstance(),
>    )
>    ```
>    *Dica: use `DebugAppCheckProviderFactory` durante o desenvolvimento local.*
> 3. Em seu OkHttp Interceptor global, você deve capturar o token (assincronamente via Coroutines) e anexá-lo ao header da requisição. Isso é **obrigatório** para todas as rotas do Game Engine (`/api/v1/sessions/*`).
>    ```kotlin
>    request.newBuilder()
>        .header("X-Firebase-AppCheck", appCheckToken)
>        .build()
>    ```

### 3.5 Proteção de Transporte (SSL Pinning)
- Para evitar ataques de *Man-In-The-Middle*, o cliente Android deve implementar **SSL/Certificate Pinning** usando o `CertificatePinner` nativo do `OkHttp`, aceitando apenas os certificados raiz do domínio da API em Go.

### 3.6 Obfuscação e Minificação (R8/ProGuard)
- O processo de build de release no Gradle deve ativar o R8 (`isMinifyEnabled = true` e `isShrinkResources = true`). Isso dificulta muito a engenharia reversa do código Kotlin/Java que manipula o anti-cheat e ofusca strings vitais.

---

## 4. Shared Architectures

- `core/`: Componentes globais de UI (Compose), temas Material 3, utilitários de Hash, OkHttp Client configurado com SSL Pinning e Interceptors (JWT Token + App Check Token).
- `feature_auth/`: Lógica de Autenticação (Firebase + ViewModels).
- `feature_quiz/`: A engine do jogo, gerenciando o estado do lote via `StateFlow`, transições de tela e exibição de componentes diferentes para Múltipla Escolha vs. Teclado Pinyin.

---

## 5. Fluxo de Autenticação e Registro de Login (App ➔ Firebase ➔ Backend Go)

Esta seção detalha o fluxo arquitetural que o App deve seguir ao lidar com o cadastro e login do usuário, garantindo a sincronização correta com o banco de dados principal no Backend (PostgreSQL) e o registro de auditoria de logins (`LoginHistory`).

A premissa básica desta arquitetura é: **O Firebase gerencia a segurança e identidade, enquanto o Backend Go gerencia as regras de negócio e dados complementares.**

### 5.1 Workflow 1: Cadastro de Usuário
O fluxo de cadastro utiliza uma arquitetura baseada no Frontend ("Frontend-Driven Sync").

1. **Ação do Usuário**: O usuário preenche o formulário no App Compose.
2. **Integração Firebase**: O App chama o SDK do Firebase (`createUserWithEmailAndPassword`) enviando **apenas** o E-mail e a Senha.
3. **Retorno do Firebase**: O Firebase cria a conta e retorna o `UID` e um **Token JWT** válido.
4. **Sincronização com o Backend (Go)**:
   - Imediatamente após receber o token, o App (via ViewModel/Repository usando `Dispatchers.IO`) faz uma chamada `POST /api/v1/users/sync` para o Backend Go.
   - **Header**: Envia o Token JWT do Firebase (`Authorization: Bearer <token_jwt>`).
   - **Body (JSON)**: Envia o restante dos dados do formulário (`nome`, `nivel`, `data_nascimento`).
5. **Processamento no Go**:
   - O Backend verifica a validade do Token.
   - O Backend realiza um UPSERT na tabela `users` do PostgreSQL.
   - **⚠️ ATENÇÃO (Race Condition)**: O Navigation Compose deve aguardar a confirmação (`200 OK`) da sincronização na API antes de rotear o usuário para a Home. Nunca baseie o redirecionamento puramente no fluxo local do `FirebaseAuth`.

### 5.2 Workflow 2: Login Recorrente e Histórico de Login (`LoginHistory`)
1. **Ação do Usuário**: O usuário loga com e-mail/senha ou Google Sign-In.
2. **Autenticação Firebase**: O App obtém um Token JWT (IdToken) fresco.
3. **Comunicação com o Backend**:
   - O App chama `POST /api/v1/auth/login`.
   - **Header**: Token JWT.
4. **Mecanismo Interno no Backend Go**:
   - O Backend valida o JWT, extrai o UID e cruza com a tabela de usuários.
   - O Backend salva os headers (IP, User-Agent) na tabela de auditoria.
   - Retorna sucesso.

### 5.3 Contrato de APIs para o App Android (Atenção Agentes 🤖)
> [!IMPORTANT]
> **Aos Agentes de Desenvolvimento (@engineer):**
> Para os workflows acima, usar Retrofit com funções `suspend` em `Dispatchers.IO` para **evitar travamentos e melhorar o Android Vitals**.
> 
> **1. Rota de Sincronização:**
> - `POST /api/v1/users/sync`
> 
> **2. Rota de Login:**
> - `POST /api/v1/auth/login`

---

## Approval Gate

> [!IMPORTANT]
> **Atenção @engineer**
> Este documento representa o design final adaptado para **Kotlin Nativo (Jetpack Compose)**.
> O backend em GO já está funcionando e localizado em `/mnt/sda2/sandbox/chinesonline/backend` (somente leitura).
> Todo o código de frontend deve estar no escopo Kotlin, com atenção obsessiva às práticas que promovam uma pontuação exemplar nos Android Vitals.

## Cronograma - Fase 1: Infraestrutura e Backend (Concluído)

- Setup do Cloud Run, Neon DB, Firebase Auth (tudo operando em PRD).

---

## 6. Integração com GameTypes (@engineer)

O jogo possui suporte a múltiplos modos de validação de resposta.

### O que o Desenvolvedor Android (@engineer) precisa saber:

1. **A regra do Default (PinyinWithoutTone)**
   - Caso o app chame a API sem o `game_type`, assume-se `pinyin_without_tone`.
2. **Início da Sessão**
   - Enviar via Retrofit Query Parameter: `GET /api/v1/sessions/new?level=1&game_type=pinyin_with_numeric_tone`
3. **Submissão de Respostas**
   - No `POST /api/v1/sessions/:id/submit`, o App **não precisa** informar o `game_type` novamente. O Backend já mapeou no banco de dados.
4. **Modos "Timed"**
   - O App deve desenhar o timer em Jetpack Compose, mas quem invalida por fraude é o backend caso ultrapasse o limite oficial (status `403`).

---

## 7. Guia de Preparação para a Apresentação do MVP (@engineer)

### 7.1 Configuração de Ambiente
- **URL Base:** Configurar no build do Gradle/Retrofit a URL de produção: `https://chinesonline-go-api-80060965106.us-east1.run.app/api/v1`

### 7.2 Headers Obrigatórios (OkHttp Interceptors)
1. `Authorization: Bearer <TOKEN>` (via suspend functions, usando cache local quando possível para não bloquear a Thread de rede desnecessariamente - **Android Vitals**).
2. `X-Firebase-AppCheck: <TOKEN>` (Mesma lógica).

### 7.3 Testando o Jogo Localmente (Performance Vitals)
- Use sempre o `Profiler` do Android Studio para assegurar que a animação da cor verde/vermelha roda instantânea (60fps), pois a validação baseada em `SHA256(salt)` acontece 100% no device de forma síncrona visualmente, porém processada via Coroutine no background thread (`Dispatchers.Default`).

---

## 8. Spaced Repetition System (SRS) e Progressão de Nível (@engineer)

O app utiliza algoritmo local para selecionar ideogramas, reduzindo custos de banco na nuvem e garantindo offline-first UI.

### 8.1 Separação de Progresso por Jogo
No backend, o nível e o score são separados por `GameType`. A API enviará de volta o `"total_score"` acumulado.

### 8.2 A Regra Matemática de Subida de Nível
- O frontend em Compose deve parar de exibir "Recorde" e passar a exibir uma "Barra de Experiência (XP)".
- Mostre um incentivo visual "Jogar Novamente" em Compose para que o usuário faça loop até que a API retorne `"leveled_up": true`.

### 8.3 Construção do Baralho (Deck Building) Client-Side

Para escalabilidade, a inteligência do Spaced Repetition fica no App Kotlin.

**Obrigações do Desenvolvedor Android (@engineer) para garantir Vitals Perfeitos:**

1. **Stack de Persistência Local (Room Database):**
   - É obrigatório o uso da biblioteca **Room** do Jetpack ou **DataStore** para a versão nativa, visando máxima velocidade (em vez do Hive que é de Flutter).
   - O acesso ao banco DEVE ser assíncrono (usando Suspend Functions no DAO) para evitar travamentos da UI Thread (**Prevenção rigorosa de ANRs - Android Vitals**).

2. **Estrutura de Dados do Room Entity:**
   - Crie uma entidade `@Entity(tableName = "ideogram_stats")`.
   ```kotlin
   @Entity(tableName = "ideogram_stats")
   data class LocalIdeogramStat(
       @PrimaryKey val id: String, // combinação de "${ideogramId}_${gameType}"
       val ideogramId: Int,
       val gameType: String,
       var correctAttempts: Int,
       var wrongAttempts: Int,
       var lastReviewed: Long
   )
   ```

3. **Algoritmo de Prioridade no Repositório (Dispatchers.Default):**
   - Ao receber o lote da API Go, o App Kotlin cruza via Room Database os IDs recebidos.
   - **Fórmula:** `Priority = (wrongAttempts * 3) - (correctAttempts)`
   - Essa junção e ordenação de listas deve rodar fora da Main Thread, preservando totalmente a fluidez visual do Compose.

4. **Submissão Assíncrona:**
   - Atualize os acertos no banco Room a cada carta virada.
   - No final, jogue a carga bruta de respostas no Retrofit `POST` de maneira leve no `Dispatchers.IO`.

---

## 9. Estilização e Layout UI (Fidelidade Rigorosa)

> [!IMPORTANT]
> **Atenção @engineer:** O aplicativo construído em Kotlin + Jetpack Compose deve ser **RIGOROSAMENTE IGUAL** ao projeto existente em Flutter. Nenhum pixel, fonte ou cor deve diferir. Toda e qualquer decisão de UI no Compose deve respeitar o design guide abaixo, tela por tela.

### 9.1 Tipografia (Google Fonts via Compose)
A fonte padrão do projeto no Flutter utiliza os estilos importados via Google Fonts.
- **Títulos / Logos:** `Lobster` (Usado para o título "ChinêsOnline", geralmente em branco ou com contraste alto). Tamanho `48sp`.
- **Textos de Conteúdo e Interface Básica:** `Vend Sans` (Usado na grande maioria dos textos, campos de input, botão principal e feedbacks de acerto).
- **Textos Secundários / Dicas (Hints):** `Sansation` (Usado para textos como "XP ACUMULADO", "Que ideograma é esse?", e hint text dos TextFields).

### 9.2 Paleta de Cores Global (Material 3 Seed: Red)
- A cor semente primária (Primary Seed) do tema é **Vermelho (`Color(0xFFF44336)`)**, baseada no `Colors.red` nativo.
- O tema roda explicitamente sobre **Material 3** (`useMaterial3 = true`), portanto, o Compose Theme deve ser gerado através do esquema de cores dinâmicas a partir dessa seed.

### 9.3 Telas: Splash, Login e Cadastro (Register)
- **Fundo / Background Global:** As três telas utilizam o mesmo gradiente de fundo (`LinearGradient`) que começa no topo esquerdo (`Alignment.TopStart`) e termina no canto inferior direito (`Alignment.BottomEnd`).
  - *Stops:* `0.0f, 0.1f, 1.0f`
  - *Cores exatas do Gradiente:* `Color(0xFF700404)`, `Color(0xFFA11010)`, `Color(0xFF450606)`.
- **SystemUI / Status Bar:** A barra de status deve ser totalmente transparente com ícones de brilho claro (Light) no Android.

- **Elementos da Tela de Splash:**
  - Animação de `FadeIn` no texto (de 0.0f a 1.0f em 2 segundos) contendo o título "ChinêsOnline" (Fonte Lobster, 48sp, `Color.White`) totalmente centralizado.

- **Elementos da Tela de Login:**
  - Título central superior: "ChinêsOnline" (Fonte Lobster, 48sp, `Color.White`).
  - Campos de Input (E-mail e Senha): Em Compose, utilize `TextField` transparente (sem caixa envolvente) apenas com a linha inferior em branco (`UnderlineInputBorder` no estilo Flutter, ou em Compose use as cores `focusedIndicatorColor` e `unfocusedIndicatorColor` em branco). Texto, hint e cursor também em branco.
  - Botão "Entrar": `Button` com cor de fundo Azul (`Color(0xFF2A7FFF)`), cor do texto `Color.White`, padding interno horizontal de 48dp, padding vertical de 12dp. Fonte `Vend Sans` ou padrão no tamanho 18sp.

- **Elementos da Tela de Cadastro (Register Screen):**
  - **Cabeçalho:** Segue a tela de Login, Título "ChinêsOnline" (Lobster, 48sp, Branco) seguido por um Subtítulo "Cadastro" (Negrito, Branco, 24sp).
  - **Campos de Texto (Nome, E-mail, Senha, Confirmar):** Exatamente o mesmo estilo do login (linha inferior branca, textos brancos).
  - **Data de Nascimento:** Funciona visualmente como um campo de texto (`TextField` read-only clicável, ou `Text` estilizado) acionando um DatePicker. O texto de "Data de Nascimento" e a data selecionada (formato DD/MM/YYYY) aparecem em branco, com a linha inferior branca.
  - **Dropdown de País:** Elemento tipo Dropdown (Exposição de lista ou bottom sheet).
    - Cor de fundo do menu suspenso aberto: `Color(0xFF450606)`.
    - Ícone da seta e textos internos: Branco.
    - Estilo de campo inativo/ativo usa a linha inferior branca como os TextFields.
  - **Botão de Submissão ("Cadastrar"):** Exatamente o mesmo estilo do botão "Entrar" (Azul `Color(0xFF2A7FFF)`, texto branco, font 18sp, paddings `48dp` x `12dp`). Loading Spinner branco ao carregar.
  - **Link inferior:** "Já possui conta? Faça Login" (TextButton com cor primária de texto Branco).
  - **Overlay de Sucesso (Crucial para fidelidade UX):** 
    - Ao concluir com sucesso, exibe uma janela pop-up / Card flutuante sobreposta próxima ao topo da tela (padding top 20dp).
    - *Background do Card:* Verde escuro (Baseado em `Colors.green.shade700` do Flutter, sugerido `#388E3C`). Raios de borda de 12dp. Sombra escura (preta) com 45% de opacidade, blur de 10 e offset Y=5. Padding interno de 20dp.
    - *Textos internos do Overlay:* 
      1. "SUCESSO" (Branco, 24sp, Negrito)
      2. "Seu cadastro foi realizado com sucesso!" (Branco, 16sp, Centralizado)
      3. "Você será redirecionado para o jogo em alguns segundos" (Branco, 14sp, Centralizado).
    - Fica visível por exatos 2 segundos e some imediatamente antes do app navegar para a próxima tela (`/quiz`).

### 9.4 Tela: Home Screen
- **AppBar (TopBar):**
  - Fundo: `Color.Black` (Preto sólido). Altura exata: 50dp.
  - Texto: "ChinêsOnline" (Branco, Negrito).
  - Ações no lado direito: Ícone de Logout e Ícone de Menu Hambúrguer (ambos em branco).
  - Status bar: Fundo Preto com ícones claros.
- **Fundo / Background (Body):**
  - Gradiente Diagonal (`TopStart` para `BottomEnd`).
  - *Stops:* `0.0f, 0.1f, 1.0f`
  - *Cores exatas do Gradiente:* `Color(0xFF046370)`, `Color(0xFF1078A1)`, `Color(0xFF064538)`.

### 9.5 Tela: Jogo / Quiz Screen (Game Engine UI - Detalhamento Rigoroso)
A tela de jogo possui 4 estados visuais distintos que devem ser gerenciados rigorosamente pelo Compose (Loading, Erro, Em Andamento e Finalizado).

- **Cores Específicas Globais do Quiz:**
  - Background Geral (Scaffold): Verde Escuro Água `Color(0xFF005662)`.
  - Verde Brilhante (Feedback de Pontos / Chip "+ 20 pts"): `Color(0xFF69F0AE)`.
  - Verde Suave (Feedback de Acerto no Card Grande): `Color(0xFFA5D6A7)`.
  - Vermelho Suave (Feedback de Erro no Card Grande): `Color(0xFFEF9A9A)`.
  - Laranja (Botão Nova Rodada / Elementos de Destaque): `Color(0xFFFF9800)`.
  - Amarelo Ouro (Estrelas / Nível): `Colors.amber` nativo / `Color(0xFFFFC107)` e `Color(0xFFFFD54F)`.
  - Botão Azul de Enviar Resposta: `Color(0xFF4285F4)`.

- **TopBar (AppBar):** 
  - Fundo estrito: `Color.Black` (elevation 0).
  - Texto centralizado/esquerdizado "ChinêsOnline" (Fonte Lobster, Branco).
  - Ações (lado direito): Ícone nativo de "Exit/Sair" (Branco) e Menu Hambúrguer (Branco).

- **Estado 1: Carregamento (Loading State)**
  - Centralizado na tela: Um `CircularProgressIndicator` nativo com a cor Branca. Fundo da tela mantém-se `0xFF005662`.

- **Estado 2: Tela de Jogo em Andamento (Gameplay State)**
  - O layout base deve estar inserido dentro de um bloco de rolagem segura (`SafeArea` / `Modifier.verticalScroll` em caso de telas pequenas).
  - **Sessão de Pontuação (Header Superior no Body):**
    - Container com *Padding Horizontal de 16dp* e *Vertical de 12dp*. Fundo transparente vazando a cor `0xFF005662`.
    - **Lado Esquerdo (Player Info):**
      - Nome do Jogador: Fonte `Vend Sans`, cor `Color(0xFFFFEEAA)` (Amarelo claro), peso 400, tamanho 16sp.
      - Abaixo do Nome: Texto "XP ACUMULADO" (Fonte `Sansation`, tamanho 10sp, cor `Color(0xFFF5F5F5)` com fontWeight 300).
      - Valor do XP: Fonte `Vend Sans`, tamanho 18sp, cor `Color(0xFFFFEEAA)`, peso 500.
    - **Lado Direito (Score e Nível):**
      - Alinhamento horizontal à direita (`End`).
      - Duas colunas lado a lado separadas por `16dp` de espaçamento.
      - **Coluna Score:** Label "SCORE" (`Sansation`, 10sp, `Color(0xFFFFEEAA)`, peso 300). Valor numérico (`Vend Sans`, 40sp, peso 600, cor Azul Claro `Color(0xFFD5FFF6)`).
      - **Coluna Nível:** Label "NÍVEL" (`Sansation`, 10sp, `Color(0xFFFFEEAA)`, peso 300). Valor numérico (`Vend Sans`, 40sp, peso 600, cor Azul Claro `Color(0xFFD5FFF6)`).

  - **Título da Pergunta:**
    - Texto "Que ideograma é esse?". Fonte `Sansation`, cor `Color.White` com opacidade de 70%, peso 400, tamanho 14sp. Margin Top 16dp.

  - **Card do Ideograma Central:**
    - Container ocupando largura máxima (com padding lateral de 16dp para não encostar na borda). Altura exata travada em `160dp`.
    - Fundo: `Color.White`. Bordas arredondadas (`RoundedCornerShape(12.dp)`).
    - Sombra rigorosa: Cor preta com 26% de opacidade (`Black26`), raio de blur de 4, Offset de Y=4.
    - O caractere do ideograma: Tamanho gigantesco `80sp`, cor `Color.Black`, perfeitamente centralizado no meio do card branco. Margin inferior ao card: 16dp.

  - **Barra de Input (Resposta do Usuário):**
    - Uma linha (Row) com Padding lateral de 16dp total, unindo o campo de texto e o botão em uma única barra conectada de altura exata `48dp`.
    - **Lado Esquerdo (TextField):** Ocupa o peso máximo da linha (`weight(1f)`). Fundo Branco `Color.White`. Raios arredondados APENAS nos cantos esquerdos (`TopStart` e `BottomStart` de 8dp). Hint text: "Digite aqui o pin yin" (Fonte `Vend Sans`, `Color.Black` com 38% de opacidade). Texto digitado (`Vend Sans`, Preto). Sem borda sublinhada!
    - **Lado Direito (Botão Enviar):** Botão fixo colado à direita. Fundo Azul `Color(0xFF4285F4)`. Raios arredondados APENAS nos cantos direitos (`TopEnd` e `BottomEnd` de 8dp). Texto "Enviar" na fonte `Vend Sans`, tamanho 18sp, peso 400, cor Branca.

  - **Card Dinâmico de Feedback (Acerto ou Erro):**
    - **Se Acerto:** Exibe primeiro um pequeno "Chip" com borda verde brilhante `Color(0xFF69F0AE)` (1.5dp de espessura) e raio 20dp, padding de 16x4dp. Texto "+ 20 pts" em itálico e negrito com a mesma cor brilhante.
    - **O Card de Detalhes:** Ocupa a largura total (padding lateral de 16dp). Borda preta sólida de 1px. Raio da borda de 8dp. Padding interno de 16dp.
    - Fundo do Card de Feedback: Verde Suave `Color(0xFFA5D6A7)` se Acerto, ou Vermelho Suave `Color(0xFFEF9A9A)` se Errado.
    - Conteúdo do Card:
      - Ícone de volume (som) no topo direito (preto).
      - Título "Correto!" ou "Incorreto!": Fonte `Vend Sans`, peso 600, tamanho 20sp, cor Preta (87% de opacidade). Centralizado.
      - Ideograma: Texto preto (87% opacidade), 28sp. Margin top 8dp.
      - Pinyin (Resposta): Fonte `Vend Sans`, itálico, peso 600, tamanho 20sp, preto 87%. Margin top 4dp.
      - Tradução: Fonte `Vend Sans`, itálico, peso 300, tamanho 14sp, preto 87%. Margin top 4dp.

- **Estado 3: Tela de Fim de Sessão / Rodada Finalizada (End Game State)**
  - O conteúdo fica centralizado horizontal e verticalmente na tela, fundo mantém-se `0xFF005662`.
  - **Se houver Level Up (Subiu de Nível):**
    - Ícone Grande de Estrela (`Icons.star` / `Icons.Filled.Star`), tamanho 64dp, cor Amarelo Ouro (`Colors.amber` nativo). Margin bottom 16dp.
    - Texto "Parabéns!\nVocê subiu para o Nível X!": Alinhamento centralizado. Fonte `Vend Sans`, cor `Color(0xFFFFD54F)` (Amber Accent), tamanho 28sp, peso Bold (Negrito).
  - **Se NÃO houver Level Up (Apenas XP Ganho):**
    - Texto "Rodada Finalizada!": Cor Branca, 24sp. Margin bottom 8dp.
    - Texto "Você ganhou +X XP": Cor Branca com 70% de opacidade, 18sp. Margin bottom 24dp.
  - **Botão Obrigatório "Nova Rodada":**
    - Fundo Laranja `Color(0xFFFF9800)`. Raio da borda de 8dp. Padding interno massivo: 32dp horizontal, 16dp vertical.
    - Texto: "Nova Rodada", cor Branca, tamanho 18sp, peso Bold (Negrito).
