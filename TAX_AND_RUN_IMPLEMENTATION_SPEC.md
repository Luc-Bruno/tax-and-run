# TAX & RUN — Especificação Mestre de Implementação para o Codex

> **Objetivo deste arquivo:** este documento é a fonte de verdade para a implementação do projeto **Tax & Run**.  
> Ele deve ser lido integralmente antes de qualquer alteração relevante no código.
>
> **Regra principal para o Codex:** não simplifique, remova ou substitua as máquinas de estado descritas aqui por `if/else`, `switch` centralizado, `enum` com lógica acoplada ou qualquer outra abordagem que descaracterize o padrão **State**.  
> A interface visual existe para representar as FSMs; ela não substitui a arquitetura exigida academicamente.

---

# 1. Contexto acadêmico

**Disciplina:** Inteligência Artificial e Ilusão de Inteligência em Jogos  
**Projeto:** Trabalho de Máquina de Estados  
**Nome do jogo:** **Tax & Run**  
**Tagline opcional:** *Work. Pay. Or run.*

O trabalho acadêmico pede agentes com estados próprios e regras determinísticas de transição, implementados com o padrão **State**.

## 1.1 Requisitos acadêmicos que DEVEM ser preservados

A implementação deve obrigatoriamente possuir:

- pelo menos dois agentes;
- Agente A com mínimo de 3 estados;
- Agente B com mínimo de 2 estados;
- padrão **State** implementado de forma clara;
- estados com métodos equivalentes a:
  - `enter`;
  - `execute`;
  - `exit` / `leave`;
- loop principal em uma classe responsável pela execução dos agentes;
- transições determinísticas baseadas em regras simples:
  - timers;
  - contadores;
  - limiares;
  - flags;
- logs claros no console que permitam acompanhar:
  - entrada em estados;
  - saída de estados;
  - transições;
  - motivo das transições;
  - eventos importantes;
- somente Java padrão na versão inicial;
- nenhuma biblioteca externa;
- comunicação entre agentes implementada para demonstrar o bônus de comunicação;
- código organizado e fácil de defender na arguição.

## 1.2 Exceção acordada com a professora

A especificação original solicita um PDF de 3–5 páginas e diagramas em `docs/`.

**Esse PDF NÃO faz parte desta versão do projeto**, pois foi acordado com a professora que a interface gráfica substituirá esse entregável.

Portanto:

- **não gerar PDF**;
- **não gastar tempo produzindo documentação em PDF**;
- diagramas em `docs/` não são prioridade nem obrigação desta implementação;
- a documentação necessária do repositório será feita principalmente por:
  - `README.md`;
  - `AGENTS.md`;
  - código organizado;
  - logs;
  - interface visual que mostre o estado atual dos agentes.

---

# 2. Resumo do jogo

**Tax & Run** é um pequeno jogo 2D em pixel art que demonstra a ilusão de inteligência produzida por máquinas de estados simples.

Existem três agentes:

1. **Worker** — trabalhador;
2. **Boss** — chefe;
3. **TaxCollector** — cobrador de impostos.

Cada agente possui sua própria máquina de estados.

O jogador controla apenas uma ação simples do Worker: **trabalhar**.

Apesar das regras serem determinísticas e simples, a interação entre os agentes deve produzir comportamentos que pareçam intencionais, como:

- o chefe perceber que o funcionário está parado;
- o chefe ficar bravo;
- o chefe perseguir o trabalhador;
- o cobrador sair do banco para cobrar imposto;
- o trabalhador fugir do cobrador;
- chefe e cobrador perseguirem o trabalhador ao mesmo tempo.

Esse comportamento emergente é parte central do conceito de **ilusão de inteligência** do trabalho.

---

# 3. Tecnologia obrigatória da primeira versão

Utilizar:

- **Java 17 ou superior**;
- Swing;
- AWT;
- Java2D;
- `BufferedImage`;
- `ImageIO`;
- `javax.swing.Timer` ou loop próprio em Java padrão;
- `javax.sound.sampled` apenas se áudio for implementado.

Não utilizar nesta versão:

- JavaFX;
- LibGDX;
- Processing;
- LWJGL;
- bibliotecas externas;
- engines externas;
- dependências Maven/Gradle externas.

## 3.1 Possível alteração futura

Caso a professora autorize JavaFX futuramente:

- manter toda a lógica do domínio;
- manter os agentes;
- manter as FSMs;
- manter as regras;
- substituir somente a camada de interface/renderização quando possível.

A arquitetura deve ser preparada para essa separação.

---

# 4. Prioridade do projeto

A ordem de prioridade é:

1. **Padrão State correto**;
2. regras e transições funcionando;
3. comunicação entre agentes;
4. logs claros;
5. jogo executável e estável;
6. interface visual;
7. polimento gráfico.

Não sacrificar a arquitetura acadêmica para obter um visual melhor.

---

# 5. Estrutura geral do gameplay

O jogo funciona em ciclos de dias.

Fluxo macro:

```text
MORNING
   ↓
todos saem de casa e vão aos seus postos
   ↓
DAY — 40 segundos
   ↓
trabalho / impostos / advertências / perseguições
   ↓
NIGHT — aproximadamente 5 segundos
   ↓
todos voltam para casa e dormem
   ↓
próximo dia
```

## 5.1 Fases globais

Criar um enum simples:

```java
public enum GamePhase {
    MORNING,
    DAY,
    NIGHT
}
```

### MORNING

- Worker sai de casa e vai para a loja;
- Boss sai de casa e vai para a loja;
- TaxCollector sai de casa e vai para o banco;
- o timer de 40 segundos ainda não começou;
- quando os três atingirem seus postos:
  - iniciar `DAY`;
  - disparar evento de início de dia;
  - TaxCollector avalia o imposto referente ao dia anterior.

### DAY

Duração:

```text
40 segundos
```

Durante DAY:

- botão `WORK!` fica disponível quando Worker pode trabalhar;
- Worker gera moedas;
- Boss monitora o trabalhador;
- TaxCollector executa cobrança;
- perseguições podem ocorrer;
- HUD mostra contagem regressiva.

### NIGHT

Duração alvo:

```text
5 segundos
```

Ao entrar em NIGHT:

- botão de trabalho é desativado;
- perseguições param;
- todos entram em estados de retorno para casa;
- agentes caminham até suas casas;
- ao chegar:
  - entram em `SLEEPING`;
  - podem exibir `Z`, `ZZ`, `ZZZ`;
- ao final dos 5 segundos:
  - garantir que todos estejam em casa;
  - incrementar o número do dia;
  - iniciar MORNING.

---

# 6. Mapa visual

O mapa deve ser uma única tela.

Elementos mínimos:

- banco;
- loja;
- três casas;
- área central para perseguições;
- rua/caminhos;
- HUD superior;
- botão `WORK!`;
- painel discreto dos estados atuais.

Sugestão de composição:

```text
┌──────────────────────────────────────────────┐
│ HUD / TIMER / DAY                            │
│                                              │
│     BANK                    SHOP             │
│                                              │
│      Tax                 Boss  Worker        │
│                                              │
│              CENTRAL AREA                    │
│           chase / circular route             │
│                                              │
│   house       house       house              │
│                                              │
│ STATE PANEL                      [ WORK! ]    │
└──────────────────────────────────────────────┘
```

---

# 7. Renderização

## 7.1 Resolução virtual recomendada

Renderizar internamente em:

```text
320 x 180
```

Escalar para:

```text
1280 x 720
```

Fator 4x.

Utilizar `RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR` para preservar pixel art.

## 7.2 Estrutura de renderização

Sugestão:

```text
JFrame
└── GamePanel extends JPanel
    └── BufferedImage virtualCanvas
        └── Graphics2D
```

`GamePanel` deve:

1. desenhar tudo em `virtualCanvas`;
2. escalar a imagem para a janela;
3. preservar nearest-neighbor;
4. evitar lógica de negócio dentro de `paintComponent`.

## 7.3 Visual inicial

O projeto deve funcionar mesmo sem arquivos de arte externos.

Portanto:

- criar arte placeholder/pixel-art simples por Java2D;
- utilizar formas, blocos e sprites gerados em código;
- opcionalmente permitir PNGs em `assets/`;
- se um asset não existir, usar fallback programático.

Isso evita que o projeto pare de funcionar por ausência de arquivos externos.

---

# 8. Controles

## 8.1 Ação principal

Botão visual:

```text
WORK!
```

Atalhos:

- clique esquerdo sobre botão;
- `SPACE` pode executar a mesma ação.

## 8.2 Ações auxiliares

Recomendado:

- `R` — reiniciar simulação;
- `P` — pausar/despausar;
- `ESC` — fechar aplicação.

Esses controles são opcionais, exceto `WORK!`.

---

# 9. Economia

## 9.1 Variáveis principais do Worker

O Worker deve manter:

```text
balance
dailyProduction
previousDayProduction
```

### balance

Saldo acumulado de moedas.

Persiste entre dias.

### dailyProduction

Quantidade produzida no dia atual.

Zera no início de cada novo DAY.

### previousDayProduction

Quantidade produzida no dia anterior.

Utilizada para calcular imposto no início do novo dia.

## 9.2 Trabalho

Cada clique válido em `WORK!`:

```text
balance += 1
dailyProduction += 1
```

Também:

- executar pequena animação de pulo;
- emitir evento `WORK_PERFORMED`;
- informar Boss para zerar seu timer de inatividade.

Não adicionar cooldown obrigatório entre cliques.

---

# 10. Regras de imposto

O imposto sempre usa a produção do **dia anterior**.

Tabela:

| Produção anterior | Resultado |
|---:|---|
| 0 | sem imposto |
| 1–20 | imposto de 5 moedas |
| 21–40 | imposto de 15 moedas |
| acima de 40 | Worker tenta fugir do TaxCollector |

## 10.1 Pagamento normal

Exemplo:

```text
previousDayProduction = 18
taxDue = 5
balance = 14
```

Resultado:

```text
balance = 9
```

TaxCollector:

```text
GOING_TO_COLLECT
→ COLLECTING
→ RETURNING_TO_BANK
→ WAITING
```

## 10.2 Saldo insuficiente

Exemplo:

```text
previousDayProduction = 3
taxDue = 5
balance = 3
```

Quando o TaxCollector chega para cobrar:

```text
Worker → FLEEING
TaxCollector → CHASING
```

Não permitir saldo negativo.

## 10.3 Produção acima de 40

Se:

```text
previousDayProduction > 40
```

o Worker decide fugir quando a cobrança efetivamente chega até ele.

Resultado:

```text
Worker → FLEEING
TaxCollector → CHASING
```

Nesse caso não ocorre pagamento.

## 10.4 Dívida entre dias

Para manter a simulação simples e evitar loops permanentes:

- a cobrança é um evento referente ao dia anterior;
- se o Worker fugir, a perseguição dura até NIGHT;
- a tentativa de cobrança daquele ciclo é encerrada no fim do dia;
- **não acumular dívida tributária para dias futuros nesta versão**.

Se essa regra for alterada futuramente, deve ser decisão explícita do usuário.

---

# 11. Regras do chefe

Boss monitora o tempo sem trabalho do Worker.

## 11.1 Timer

Limite:

```text
5 segundos
```

Sempre que ocorre `WORK_PERFORMED`:

```text
bossInactivityTimer = 0
```

## 11.2 Primeira ocorrência

Se passarem 5 segundos sem trabalho:

```text
warningCountToday = 1
Boss → ANGRY
```

Visual:

- `!` sobre o Boss;
- pequeno pulo;
- expressão/efeito de raiva.

Worker pode ficar em `IDLE`.

Após curta animação de raiva:

```text
Boss → WATCHING
```

## 11.3 Segunda ocorrência

Se novamente passarem 5 segundos sem trabalho:

```text
warningCountToday = 2
Boss → ANGRY
```

Após a curta animação:

```text
Boss → CHASING
Worker → FLEEING
```

## 11.4 Persistência das advertências

Trabalhar:

```text
zera o timer de 5 segundos
```

mas:

```text
NÃO zera warningCountToday
```

As advertências zeram apenas no início de um novo dia.

---

# 12. Perseguições

## 12.1 Regra geral

Worker pode entrar em `FLEEING` por:

- Boss;
- TaxCollector;
- ambos.

Durante `FLEEING`:

- `WORK!` fica desativado;
- Worker não produz moedas;
- Worker corre pela região central;
- agentes perseguidores correm atrás dele.

## 12.2 Rota de fuga

Não implementar pathfinding complexo.

Utilizar:

- conjunto de waypoints;
- caminho circular/retangular na área central;
- Worker percorre continuamente esses pontos;
- perseguidores seguem a posição atual do Worker.

Exemplo:

```text
P1 → P2 → P3 → P4 → P5 → P6 → P1
```

Objetivo visual:

```text
Boss ─┐
      ↓
   Worker → → →
      ↑       ↓
Tax ──┘   ← ←
```

## 12.3 Captura

Nesta versão:

- não existe estado de “capturado”;
- perseguição é uma demonstração comportamental;
- dura até NIGHT.

Isso evita introduzir mecânicas que não fazem parte do escopo acadêmico.

## 12.4 Perseguição dupla

Deve ser suportado:

```text
Worker = FLEEING
Boss = CHASING
TaxCollector = CHASING
```

Esse é um dos exemplos principais de comportamento emergente.

---

# 13. Comunicação entre agentes

A comunicação entre agentes deve ser explícita no código.

Recomendação:

```text
GameEventBus
```

ou sistema equivalente simples usando apenas Java padrão.

## 13.1 Eventos sugeridos

```text
DAY_STARTED
NIGHT_STARTED
WORK_PERFORMED
BOSS_WARNING
BOSS_CHASE_STARTED
TAX_COLLECTION_REQUESTED
TAX_PAYMENT_SUCCEEDED
TAX_EVASION_STARTED
TAX_COLLECTOR_CHASE_STARTED
```

## 13.2 Exemplo acadêmico de comunicação

```text
Worker executa trabalho
    ↓
WORK_PERFORMED
    ↓
Boss recebe evento
    ↓
zera timer de inatividade
```

Outro exemplo:

```text
Boss chega à segunda advertência
    ↓
BOSS_CHASE_STARTED
    ↓
Worker recebe evento
    ↓
Worker muda para FLEEING
```

Outro:

```text
TaxCollector identifica evasão/falta de saldo
    ↓
TAX_COLLECTOR_CHASE_STARTED
    ↓
Worker muda para FLEEING
```

Isso deve ser visível nos logs.

---

# 14. Padrão State — contrato obrigatório

Criar uma interface semelhante a:

```java
public interface State<T> {

    void enter(T owner, GameContext context);

    void execute(T owner, GameContext context, double deltaTime);

    void exit(T owner, GameContext context);

    default void onEvent(T owner, GameContext context, GameEvent event) {
    }

    String getName();
}
```

Os nomes podem variar levemente, mas os conceitos de:

```text
enter
execute
exit
```

são obrigatórios.

## 14.1 StateMachine

Recomendado criar:

```java
StateMachine<T>
```

Responsabilidades:

- armazenar estado atual;
- chamar `enter`;
- chamar `execute`;
- chamar `exit`;
- efetuar transições;
- delegar eventos;
- gerar logs das transições.

Exemplo conceitual:

```java
changeState(newState, "5 seconds without work");
```

deve gerar algo semelhante a:

```text
[DAY 2][BOSS] WATCHING -> ANGRY | reason=5 seconds without work
```

## 14.2 Proibições arquiteturais

Não implementar a FSM principal como:

```java
switch(currentState) {
    ...
}
```

Não concentrar toda a lógica em:

```java
GameController
```

Não usar:

```text
enum + if/else gigante
```

como substituição do padrão State.

Enums podem existir apenas para:

- GamePhase;
- EventType;
- ChaseReason;
- IDs/constantes.

---

# 15. FSM do Worker

Estados mínimos recomendados:

```text
SLEEPING
GOING_TO_WORK
WORKING
IDLE
FLEEING
GOING_HOME
```

## 15.1 SLEEPING

### enter

- marcar como dormindo;
- exibir `Z`;
- garantir posição residencial se necessário.

### execute

Se fase mudar para MORNING:

```text
→ GOING_TO_WORK
```

### exit

- remover indicador de sono.

---

## 15.2 GOING_TO_WORK

### enter

- definir alvo = posição da loja.

### execute

- movimentar até a loja;
- permanecer pronto no local;
- quando `GamePhase == DAY`:

```text
→ WORKING
```

### exit

- limpar alvo de deslocamento.

---

## 15.3 WORKING

### enter

- habilitar trabalho;
- animação idle/work.

### execute

- aguardar entrada do jogador;
- responder a NIGHT;
- responder a eventos de perseguição.

### evento WORK

Se válido:

```text
balance++
dailyProduction++
```

e emitir:

```text
WORK_PERFORMED
```

### transições

Primeira advertência do Boss:

```text
→ IDLE
```

Perseguição:

```text
→ FLEEING
```

NIGHT:

```text
→ GOING_HOME
```

---

## 15.4 IDLE

Representa que o Worker já ficou tempo demais sem trabalhar.

### enter

- animação ociosa;
- opcionalmente exibir símbolo pequeno.

### execute

Se jogador trabalhar:

```text
→ WORKING
```

Se segunda advertência provocar perseguição:

```text
→ FLEEING
```

Se TaxCollector iniciar perseguição:

```text
→ FLEEING
```

NIGHT:

```text
→ GOING_HOME
```

---

## 15.5 FLEEING

### enter

- desabilitar `WORK!`;
- selecionar waypoint inicial de fuga;
- ativar animação de corrida.

### execute

- mover pelo circuito de fuga;
- alterar waypoint ao atingir alvo;
- aceitar múltiplos perseguidores;
- NIGHT:

```text
→ GOING_HOME
```

### exit

- limpar rota de fuga;
- reabilitar comportamento normal apenas quando aplicável.

---

## 15.6 GOING_HOME

### enter

- alvo = casa do Worker.

### execute

- mover até casa;
- ao chegar:

```text
→ SLEEPING
```

---

# 16. FSM do Boss

Estados:

```text
SLEEPING
GOING_TO_WORK
WATCHING
ANGRY
CHASING
GOING_HOME
```

## 16.1 SLEEPING

MORNING:

```text
→ GOING_TO_WORK
```

---

## 16.2 GOING_TO_WORK

Mover até a loja.

Quando DAY iniciar:

```text
→ WATCHING
```

---

## 16.3 WATCHING

Responsável pelo timer de inatividade.

### evento WORK_PERFORMED

```text
inactivityTimer = 0
```

### execute

Enquanto DAY:

```text
inactivityTimer += deltaTime
```

Quando:

```text
inactivityTimer >= 5
```

então:

```text
inactivityTimer = 0
warningCountToday++
→ ANGRY
```

---

## 16.4 ANGRY

### enter

- mostrar `!`;
- pulo curto;
- iniciar `angerTimer`.

### execute

Após aproximadamente:

```text
0.8 segundos
```

Se:

```text
warningCountToday >= 2
```

então:

```text
→ CHASING
```

e emitir:

```text
BOSS_CHASE_STARTED
```

Senão:

```text
→ WATCHING
```

---

## 16.5 CHASING

### enter

- animação de corrida;
- alvo dinâmico = Worker.

### execute

- perseguir Worker;
- NIGHT:

```text
→ GOING_HOME
```

---

## 16.6 GOING_HOME

Mover até casa.

Ao chegar:

```text
→ SLEEPING
```

---

# 17. FSM do TaxCollector

Estados:

```text
SLEEPING
GOING_TO_BANK
WAITING
GOING_TO_COLLECT
COLLECTING
RETURNING_TO_BANK
CHASING
GOING_HOME
```

## 17.1 SLEEPING

MORNING:

```text
→ GOING_TO_BANK
```

---

## 17.2 GOING_TO_BANK

Mover até o banco.

Quando DAY iniciar:

```text
→ WAITING
```

---

## 17.3 WAITING

Ao receber `DAY_STARTED`:

- calcular regra do imposto com `previousDayProduction`;
- se imposto = 0:
  - continuar `WAITING`;
- se existe cobrança:
  - `→ GOING_TO_COLLECT`.

---

## 17.4 GOING_TO_COLLECT

Mover fisicamente do banco até o Worker.

Quando entrar em distância de cobrança:

```text
→ COLLECTING
```

---

## 17.5 COLLECTING

Avaliar:

### Caso 1 — previousDayProduction > 40

Emitir:

```text
TAX_EVASION_STARTED
TAX_COLLECTOR_CHASE_STARTED
```

TaxCollector:

```text
→ CHASING
```

Worker:

```text
→ FLEEING
```

### Caso 2 — saldo insuficiente

Se:

```text
balance < taxDue
```

mesmo resultado:

```text
TaxCollector → CHASING
Worker → FLEEING
```

### Caso 3 — pagamento possível

Deduzir:

```text
balance -= taxDue
```

Exibir feedback visual:

```text
-$5
```

ou:

```text
-$15
```

Emitir:

```text
TAX_PAYMENT_SUCCEEDED
```

e:

```text
→ RETURNING_TO_BANK
```

---

## 17.6 RETURNING_TO_BANK

Mover de volta ao banco.

Ao chegar:

```text
→ WAITING
```

---

## 17.7 CHASING

Perseguir Worker até NIGHT.

NIGHT:

```text
→ GOING_HOME
```

---

## 17.8 GOING_HOME

Mover até a residência.

Ao chegar:

```text
→ SLEEPING
```

---

# 18. Reset diário

Ao iniciar um novo dia:

```text
previousDayProduction = dailyProduction do dia anterior
dailyProduction = 0
warningCountToday = 0
bossInactivityTimer = 0
taxAttemptedToday = false
bossChasing = false
taxCollectorChasing = false
```

O saldo:

```text
balance
```

NÃO deve ser zerado.

Dia 1:

```text
previousDayProduction = 0
```

portanto:

```text
sem imposto
```

---

# 19. Movimento

Criar uma estrutura simples de posição:

```java
class Vector2 {
    double x;
    double y;
}
```

ou equivalente.

Método útil:

```text
moveTowards(target, speed, deltaTime)
```

Cada agente deve ter:

```text
position
speed
target
```

Não implementar física complexa.

Não implementar colisão de mundo complexa.

O objetivo é visualização de estados, não simulação física.

---

# 20. Velocidades sugeridas

Os valores podem ser ajustados visualmente.

Exemplo em coordenadas virtuais:

```text
NORMAL_MOVE_SPEED = 30–40 px/s
WORKER_FLEE_SPEED = 55 px/s
BOSS_CHASE_SPEED = 48–52 px/s
TAX_CHASE_SPEED = 48–52 px/s
```

Worker deve ser rápido o suficiente para manter a perseguição visual sem ser imediatamente alcançado.

---

# 21. HUD

A interface deve funcionar também como documentação visual da FSM.

Exibir sempre:

```text
TAX & RUN
Day: N
Phase: MORNING / DAY / NIGHT
Day timer
Balance
Production today
Previous day production
Warnings today
Tax due/current tax status
```

Painel de estados:

```text
Worker: WORKING
Boss: WATCHING
TaxCollector: WAITING
```

Esse painel é importante para a arguição.

## 21.1 Barra do dia

Durante DAY:

```text
40s → 0s
```

barra visual diminuindo.

Durante NIGHT:

- mostrar lua;
- opcionalmente nova barra de 5s.

Durante MORNING:

- mostrar “Day N” / nascer do sol / “Going to work”.

---

# 22. Feedback visual

Implementar feedback simples:

Worker trabalhando:

```text
pequeno pulo
+1 moeda
```

Boss bravo:

```text
!
pequeno pulo
```

TaxCollector cobrando:

```text
$5
```

ou:

```text
$15
```

Pagamento:

```text
-5
```

ou:

```text
-15
```

Worker fugindo:

```text
!
```

Dormindo:

```text
Z
ZZ
ZZZ
```

---

# 23. Logs obrigatórios

Todos os logs devem ser claros.

Formato recomendado:

```text
[12:31:02.153][DAY 2][WORKER] WORKING -> IDLE | reason=boss first warning
[12:31:07.201][DAY 2][BOSS] WATCHING -> ANGRY | reason=5s without work
[12:31:08.004][DAY 2][BOSS] ANGRY -> WATCHING | reason=first warning completed
```

Eventos:

```text
[DAY 2][EVENT] WORK_PERFORMED | source=WORKER
[DAY 2][EVENT] BOSS_WARNING | count=1
[DAY 2][EVENT] TAX_COLLECTOR_CHASE_STARTED | reason=INSUFFICIENT_FUNDS
```

Cobrança:

```text
[DAY 3][TAX] previousProduction=18 due=5 balanceBefore=22
[DAY 3][TAX] payment successful balanceAfter=17
```

Não imprimir log a cada frame.

Priorizar:

- transições;
- eventos;
- início/fim de dia;
- trabalho;
- imposto;
- advertências;
- perseguições.

---

# 24. Estrutura de código recomendada

```text
TaxAndRun/
│
├── README.md
├── AGENTS.md
├── .gitignore
├── assets/
│   └── optional png assets
│
└── src/
    └── taxandrun/
        ├── Main.java
        │
        ├── game/
        │   ├── GameLoop.java
        │   ├── GameController.java
        │   ├── GameContext.java
        │   ├── GameConfig.java
        │   └── GamePhase.java
        │
        ├── state/
        │   ├── State.java
        │   └── StateMachine.java
        │
        ├── event/
        │   ├── GameEvent.java
        │   ├── GameEventType.java
        │   └── GameEventBus.java
        │
        ├── agent/
        │   ├── Agent.java
        │   ├── Worker.java
        │   ├── Boss.java
        │   ├── TaxCollector.java
        │   │
        │   ├── worker/
        │   │   ├── WorkerSleepingState.java
        │   │   ├── WorkerGoingToWorkState.java
        │   │   ├── WorkerWorkingState.java
        │   │   ├── WorkerIdleState.java
        │   │   ├── WorkerFleeingState.java
        │   │   └── WorkerGoingHomeState.java
        │   │
        │   ├── boss/
        │   │   ├── BossSleepingState.java
        │   │   ├── BossGoingToWorkState.java
        │   │   ├── BossWatchingState.java
        │   │   ├── BossAngryState.java
        │   │   ├── BossChasingState.java
        │   │   └── BossGoingHomeState.java
        │   │
        │   └── tax/
        │       ├── TaxSleepingState.java
        │       ├── TaxGoingToBankState.java
        │       ├── TaxWaitingState.java
        │       ├── TaxGoingToCollectState.java
        │       ├── TaxCollectingState.java
        │       ├── TaxReturningToBankState.java
        │       ├── TaxChasingState.java
        │       └── TaxGoingHomeState.java
        │
        ├── world/
        │   ├── Vector2.java
        │   └── WorldLayout.java
        │
        └── ui/
            ├── GameWindow.java
            ├── GamePanel.java
            ├── GameRenderer.java
            ├── InputHandler.java
            ├── AssetManager.java
            └── PixelArtFactory.java
```

A estrutura exata pode ser adaptada se necessário, mas:

- lógica dos estados;
- lógica do jogo;
- renderização;
- eventos;
- entrada;

devem permanecer separados.

---

# 25. Responsabilidades das classes principais

## Main

- inicializar aplicação;
- criar janela;
- iniciar GameController/GameLoop.

## GameLoop

Responsável pelo loop principal exigido academicamente.

Deve:

- calcular `deltaTime`;
- processar input;
- atualizar GameController;
- disparar repaint.

## GameController

Orquestra:

- fase global;
- timer;
- agentes;
- eventos;
- reset diário;
- input de trabalho.

Não deve conter toda a lógica interna dos estados.

## GameContext

Referência compartilhada segura para:

- fase;
- timer;
- layout;
- agentes;
- EventBus;
- dados compartilhados estritamente necessários.

## StateMachine

Gerencia o estado atual de um agente.

## Agent

Base compartilhada:

- nome;
- posição;
- velocidade;
- utilidades de movimento.

## GameRenderer

Responsável apenas por desenho.

## InputHandler

Converte mouse/teclado em ações de alto nível.

Exemplo:

```text
WORK_PRESSED
RESET
PAUSE
```

---

# 26. Ordem de atualização por frame

Recomendação:

```text
1. calcular deltaTime
2. processar input
3. atualizar fase global
4. despachar eventos pendentes
5. atualizar Worker
6. atualizar Boss
7. atualizar TaxCollector
8. despachar novos eventos
9. atualizar animações visuais
10. repaint
```

Evitar dependência acidental da ordem.

O EventBus deve ser usado para comunicação explícita.

---

# 27. Interface visual e lógica

Regra arquitetural importante:

```text
Renderer NÃO decide estado.
Renderer NÃO cobra imposto.
Renderer NÃO muda contador.
Renderer NÃO controla IA.
```

Renderer apenas lê dados e desenha.

Exemplo correto:

```text
Boss state = ANGRY
        ↓
Renderer desenha !
```

Exemplo incorreto:

```text
Renderer detecta 5 segundos
        ↓
Renderer muda Boss para ANGRY
```

---

# 28. Animações

Animações simples bastam.

Não criar um sistema de animação excessivamente complexo antes do MVP.

Animações necessárias:

- idle;
- walk;
- work jump;
- angry jump;
- run;
- sleep.

Podem inicialmente ser:

- offsets de posição;
- alternância entre 2 frames;
- formas programáticas.

---

# 29. Cenários obrigatórios para teste manual

Antes de considerar o projeto pronto, testar todos.

## Cenário 1 — trabalho normal

Durante DAY:

- clicar em `WORK!` antes de cada período de 5 segundos;
- Boss nunca deve perseguir;
- moedas devem aumentar.

Esperado:

```text
Worker = WORKING
Boss = WATCHING
```

---

## Cenário 2 — primeira advertência

- não trabalhar por 5 segundos.

Esperado:

```text
Boss = ANGRY
Worker = IDLE
warningCountToday = 1
```

Depois Boss volta para WATCHING.

---

## Cenário 3 — segunda advertência

- receber primeira advertência;
- trabalhar ou aguardar;
- deixar passar mais 5 segundos sem trabalho.

Esperado:

```text
Boss → CHASING
Worker → FLEEING
```

---

## Cenário 4 — imposto de 5

Dia anterior:

```text
dailyProduction = 10
```

Novo dia:

```text
taxDue = 5
```

TaxCollector deve:

```text
WAITING
→ GOING_TO_COLLECT
→ COLLECTING
→ RETURNING_TO_BANK
→ WAITING
```

---

## Cenário 5 — imposto de 15

Dia anterior:

```text
dailyProduction = 25
```

Novo dia:

```text
taxDue = 15
```

Fluxo normal de cobrança.

---

## Cenário 6 — saldo insuficiente

Dia anterior:

```text
dailyProduction = 3
balance = 3
```

Novo dia:

```text
taxDue = 5
```

Esperado:

```text
Worker → FLEEING
TaxCollector → CHASING
```

---

## Cenário 7 — acima de 40

Dia anterior:

```text
dailyProduction = 41+
```

Novo dia:

quando TaxCollector chegar:

```text
Worker → FLEEING
TaxCollector → CHASING
```

---

## Cenário 8 — perseguição dupla

Iniciar fuga do TaxCollector.

Como Worker não consegue trabalhar:

- Boss continua monitorando;
- após duas advertências:

```text
Boss → CHASING
```

Esperado:

```text
Worker = FLEEING
Boss = CHASING
TaxCollector = CHASING
```

---

## Cenário 9 — noite

Durante qualquer situação:

- timer de DAY chega a zero.

Esperado:

```text
todos → GOING_HOME
```

Perseguições encerram.

Depois:

```text
todos → SLEEPING
```

Novo dia inicia corretamente.

---

# 30. README.md obrigatório

O Codex deve criar um `README.md`.

Ele deve conter no mínimo:

## 30.1 Título

```text
Tax & Run
```

## 30.2 Contexto

Explicar brevemente:

- disciplina;
- máquina de estados;
- ilusão de inteligência;
- três agentes.

## 30.3 Agentes

Explicar:

- Worker;
- Boss;
- TaxCollector.

## 30.4 Estados

Listar os principais estados de cada agente.

## 30.5 Como compilar

Fornecer comandos compatíveis com Java padrão.

Exemplo PowerShell:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$files = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac -d out $files
```

## 30.6 Como executar

Exemplo:

```powershell
java -cp out taxandrun.Main
```

Adaptar ao package final utilizado.

## 30.7 Controles

Listar:

```text
Mouse / SPACE = Work
R = Reset
P = Pause
ESC = Exit
```

se implementados.

## 30.8 Como observar as FSMs

Explicar:

- painel visual de estados;
- logs do console;
- exemplos de transição.

## 30.9 Dependências

Declarar explicitamente:

```text
Nenhuma biblioteca externa.
Apenas Java padrão, Swing, AWT e Java2D.
```

## 30.10 Observação sobre documentação

Não afirmar que “faltou PDF”.

Apenas documentar o projeto normalmente.

---

# 31. AGENTS.md obrigatório

Criar `AGENTS.md` na raiz.

Esse arquivo é destinado aos agentes de IA/Codex que trabalharão no repositório.

Ele deve registrar de forma objetiva:

- projeto = Tax & Run;
- Java padrão;
- sem dependências externas;
- padrão State obrigatório;
- três agentes;
- regras de negócio;
- duração DAY = 40s;
- NIGHT = 5s;
- 5s de inatividade;
- duas advertências;
- impostos;
- perseguições;
- comunicação por eventos;
- HUD com estados;
- logs;
- proibição de remover as FSMs;
- proibição de migrar para JavaFX sem autorização explícita;
- obrigação de atualizar `README.md` e `AGENTS.md` se alguma regra for alterada.

## 31.1 Texto conceitual que deve constar no AGENTS.md

Algo equivalente a:

> O padrão State é requisito acadêmico e não deve ser simplificado.  
> Não substituir estados concretos por um switch centralizado, enum comportamental ou conjunto de if/else.  
> Alterações de regras de negócio devem ser confirmadas com o usuário.  
> A camada gráfica não deve conter lógica de decisão dos agentes.

---

# 32. .gitignore

Adicionar no mínimo:

```text
out/
*.class
.idea/
.vscode/
.DS_Store
```

Ajustar conforme ambiente.

---

# 33. Estratégia de implementação para o Codex

Implementar incrementalmente.

## Fase 1 — núcleo acadêmico

Criar:

- agentes;
- State;
- StateMachine;
- GameLoop;
- GameContext;
- eventos;
- logs.

Executar ainda que com interface mínima.

Objetivo:

```text
FSMs funcionando corretamente
```

## Fase 2 — ciclo global

Implementar:

- MORNING;
- DAY;
- NIGHT;
- timer;
- reset diário.

## Fase 3 — Worker + Boss

Implementar:

- trabalho;
- moedas;
- timer de 5s;
- advertências;
- perseguição.

## Fase 4 — TaxCollector

Implementar:

- produção anterior;
- cálculo do imposto;
- deslocamento físico para cobrança;
- pagamento;
- fuga;
- perseguição.

## Fase 5 — comportamento emergente

Validar:

```text
Boss + TaxCollector perseguindo Worker
```

## Fase 6 — interface

Implementar:

- mapa;
- sprites;
- HUD;
- botão;
- timer;
- estado dos agentes.

## Fase 7 — polimento

Adicionar:

- noite;
- efeitos;
- animações;
- indicadores;
- pixel art melhor.

## Fase 8 — documentação

Finalizar:

- README.md;
- AGENTS.md;
- comentários apenas onde úteis;
- logs claros.

---

# 34. Regras para evitar overengineering

Não implementar sem necessidade:

- banco de dados;
- rede;
- multiplayer;
- save game;
- pathfinding A*;
- física;
- engine ECS;
- reflection;
- dependency injection framework;
- scripting;
- árvore de comportamento;
- machine learning;
- threads complexas;
- plugins.

O objetivo é demonstrar máquinas de estado.

---

# 35. Critérios de aceitação

O projeto só pode ser considerado pronto quando todos forem verdadeiros.

## Arquitetura

- [ ] Java padrão.
- [ ] Sem bibliotecas externas.
- [ ] State interface com enter/execute/exit.
- [ ] StateMachine real.
- [ ] Cada agente possui estados concretos.
- [ ] GameLoop separado.
- [ ] Renderização separada da lógica.

## Agentes

- [ ] Worker funciona.
- [ ] Boss funciona.
- [ ] TaxCollector funciona.
- [ ] Cada um possui FSM própria.
- [ ] Comunicação entre agentes funciona.

## Gameplay

- [ ] DAY dura 40s.
- [ ] NIGHT dura ~5s.
- [ ] Worker ganha 1 moeda por trabalho.
- [ ] Boss reage após 5s.
- [ ] Segunda advertência gera perseguição.
- [ ] Imposto 1–20 = 5.
- [ ] Imposto 21–40 = 15.
- [ ] Acima de 40 gera fuga.
- [ ] Saldo insuficiente gera fuga.
- [ ] TaxCollector caminha fisicamente até Worker.
- [ ] Perseguição dupla é possível.
- [ ] Todos dormem à noite.

## Interface

- [ ] Janela gráfica funcional.
- [ ] Pixel art ou visual pixelizado.
- [ ] HUD mostra estados.
- [ ] Timer visível.
- [ ] Botão WORK!.
- [ ] Feedback visual.
- [ ] Interface continua responsiva.

## Logs

- [ ] Transições aparecem no console.
- [ ] Motivos aparecem no console.
- [ ] Impostos aparecem no console.
- [ ] Eventos entre agentes aparecem no console.

## Repositório

- [ ] README.md.
- [ ] AGENTS.md.
- [ ] .gitignore.
- [ ] src/ organizado.
- [ ] Nenhum PDF obrigatório.
- [ ] Nenhuma dependência externa.

---

# 36. Requisitos para facilitar a arguição

O código deve ser fácil de explicar oralmente.

Priorizar nomes claros.

Exemplos:

```text
WorkerWorkingState
BossAngryState
TaxGoingToCollectState
StateMachine.changeState()
GameEventBus.publish()
```

Evitar nomes genéricos como:

```text
Manager2
HandlerX
Thing
Helper
DoStuff
```

Durante a defesa, deve ser possível mostrar:

1. interface `State`;
2. `StateMachine`;
3. um agente;
4. dois ou três estados concretos;
5. GameLoop;
6. comunicação por eventos;
7. logs correspondentes na execução;
8. a mesma transição acontecendo visualmente.

---

# 37. Exemplo de explicação acadêmica do projeto

A implementação deve sustentar tecnicamente a seguinte explicação:

> Cada personagem possui uma máquina de estados independente.  
> Os estados implementam os métodos de entrada, execução e saída.  
> As transições são determinísticas e dependem de timers, contadores, valores econômicos e eventos.  
> A comunicação entre agentes faz com que uma mudança de comportamento em um personagem produza reações nos demais.  
> Apesar de nenhum personagem utilizar IA complexa, a combinação dessas regras gera a impressão de intenção e comportamento inteligente.

---

# 38. Comportamento emergente que deve ser preservado

Um cenário importante:

```text
Worker produziu mais de 40 moedas
        ↓
novo dia
        ↓
TaxCollector sai do banco
        ↓
vai até Worker
        ↓
Worker foge
        ↓
TaxCollector persegue
        ↓
Worker deixa de trabalhar
        ↓
Boss registra inatividade
        ↓
primeira advertência
        ↓
segunda advertência
        ↓
Boss começa a perseguir
        ↓
Boss + TaxCollector perseguem Worker
```

Não criar uma regra do tipo:

```text
if taxCollectorChasing:
    bossChasing = true;
```

O Boss deve começar a perseguição por **sua própria regra de inatividade**.

Esse detalhe é essencial para caracterizar comportamento emergente.

---

# 39. Política de mudanças

O Codex NÃO deve alterar sozinho:

- tempos principais;
- faixas de imposto;
- quantidade de advertências;
- agentes;
- regras de perseguição;
- arquitetura State;
- tecnologia da interface.

Se encontrar problema real:

1. preservar o comportamento definido;
2. implementar a solução técnica mais simples;
3. documentar a decisão;
4. perguntar ao usuário apenas se houver conflito real de regra.

---

# 40. Instrução final para o Codex

Ao receber este documento:

1. leia tudo antes de codificar;
2. inspecione o repositório existente;
3. preserve arquivos válidos já existentes;
4. crie/atualize `AGENTS.md`;
5. crie/atualize `README.md`;
6. implemente primeiro o núcleo acadêmico;
7. valide cada FSM;
8. depois implemente a interface;
9. rode o projeto;
10. corrija erros de compilação e execução;
11. valide os nove cenários de teste manual;
12. não finalize enquanto os critérios de aceitação não estiverem atendidos.

**Fonte de verdade do projeto:** este documento + `AGENTS.md`.

O resultado final deve ser um pequeno jogo 2D funcional, visualmente agradável, executável apenas com Java padrão e capaz de demonstrar claramente o padrão State, a comunicação entre agentes e a ilusão de inteligência em jogos.
