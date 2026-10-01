# Tax & Run

Projeto acadêmico da disciplina **Inteligência Artificial e Ilusão de Inteligência em Jogos**: um jogo 2D que demonstra como máquinas de estados e comunicação entre agentes produzem comportamentos aparentemente inteligentes.

## Situação do projeto

Versão jogável com três máquinas de estados, comunicação por eventos, ciclo de dias e interface em Swing. O código está organizado em **Model, View e Controller (MVC)**, preservando o padrão State dentro do Model. O visual usa pixel art programática.

Os nove cenários da especificação têm cobertura automatizada. A revisão passou em **249 verificações**, além do teste de abertura, atualização e fechamento de uma janela real. O escopo e as evidências estão em [docs/VALIDACAO.md](docs/VALIDACAO.md).

A especificação completa está em [TAX_AND_RUN_IMPLEMENTATION_SPEC.md](TAX_AND_RUN_IMPLEMENTATION_SPEC.md). As orientações para trabalhar no código estão em [AGENTS.md](AGENTS.md).

## Proposta

O jogador trabalha por meio do botão `WORK!`, recebendo uma moeda por ação válida. Três agentes interagem por regras determinísticas:

- **Worker:** trabalha, fica ocioso e foge quando começa uma perseguição.
- **Boss:** monitora a inatividade, dá advertências e persegue após a segunda advertência do dia.
- **TaxCollector:** cobra o imposto calculado sobre a produção do dia anterior e persegue em caso de fuga.

Cada agente tem sua própria máquina de estados, implementada com o padrão **State** e os métodos `enter`, `execute` e `exit`. Cada estado é uma classe concreta.

## Estados

| Agente | Estados |
| --- | --- |
| Worker | SLEEPING, GOING_TO_WORK, WORKING, IDLE, FLEEING, GOING_HOME |
| Boss | SLEEPING, GOING_TO_WORK, WATCHING, ANGRY, CHASING, GOING_HOME |
| TaxCollector | SLEEPING, GOING_TO_BANK, WAITING, GOING_TO_COLLECT, COLLECTING, RETURNING_TO_BANK, CHASING, GOING_HOME |

## Regras principais

- Ciclo global: `MORNING` → `DAY` → `NIGHT`.
- Pela manhã, todos chegam aos postos e depois o cobrador resolve o imposto. Só então começam os 40 segundos do dia, o trabalho e o timer do chefe. Se não houver imposto, o dia começa assim que todos chegam aos postos.
- O pagamento ou a fuga concluem a cobrança; não é necessário esperar o cobrador retornar ao banco. Em caso de fuga, o relógio começa, mas o Worker continua sem poder trabalhar.
- À noite, todos voltam para casa. O contador fica em 5 segundos até todos chegarem e entrarem em SLEEPING; depois são contados 5 segundos completos de sono.
- Cada ação válida de trabalho aumenta o saldo e a produção diária em 1.
- O chefe reage após 5 segundos sem trabalho. Trabalhar reinicia o timer, mas as advertências só zeram no próximo dia.
- Imposto sobre a produção anterior: 0 → sem cobrança; 1–20 → 5 moedas; 21–40 → 15 moedas; acima de 40 → fuga quando o cobrador chega.
- Saldo insuficiente também provoca fuga. Não existe saldo negativo nem dívida acumulada entre dias.
- Perseguições duram até a noite, sem captura. Chefe e cobrador podem perseguir ao mesmo tempo, cada um por sua própria regra.

## Tecnologia

**Java 17 ou superior**, usando somente Java padrão, Swing, AWT e Java2D. Essa é a tecnologia definida para o projeto, sem bibliotecas externas. A migração para JavaFX foi descartada.

A interface mostra os estados atuais dos agentes, o tempo, o saldo, a produção, as advertências e a situação da cobrança. Logs do console registram entradas, saídas, transições, motivos e comunicação entre agentes. A renderização fica separada da lógica do jogo.

O mapa é desenhado em 320 × 180 e ampliado com nearest-neighbor. Sprites e uma fonte pixelada própria são desenhados em código; o jogo funciona sem arquivos de arte ou fontes externas.

## Compilação, execução e controles

No Windows, dê dois cliques em **`run.bat` no Explorador de Arquivos**. Ele compila o código, abre a janela e mantém os logs no console. Abrir o arquivo pelo editor mostra seu código; isso não executa o jogo.

Também é possível executar no PowerShell, a partir da pasta do projeto:

```powershell
.\run.ps1
```

Os scripts procuram um **JDK 17+** em `JAVA_HOME`, no `PATH` e na pasta pessoal `.jdks`. Não alteram o Java padrão do Windows. Uma instalação apenas do Java 8, sem `javac`, não é suficiente. Para escolher um JDK explicitamente:

```powershell
.\run.ps1 -JavaHome 'C:\caminho\para\jdk-17'
```

Se a política do PowerShell impedir scripts locais, `run.bat` já executa o script com permissão limitada ao processo que abre. Não é necessário alterar a política global.

Para compilar e executar diretamente, com JDK 17+ no PATH:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$javaSources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
javac --release 17 -encoding UTF-8 -d out $javaSources
java -cp out taxandrun.Main
```

| Controle | Ação |
| --- | --- |
| Clique esquerdo em WORK! | Trabalhar e ganhar 1 moeda |
| SPACE | A mesma ação de trabalho, uma vez por pressão |
| P | Pausar ou retomar |
| R | Reiniciar a simulação desde o primeiro dia |
| ESC | Fechar a janela |

O botão fica indisponível durante a manhã (inclusive durante a cobrança), a noite, a pausa e a fuga. Cliques na preparação são descartados e não geram trabalho depois. No estado IDLE, o primeiro clique já retoma o trabalho e concede uma moeda. Não há cooldown entre cliques.

## Como observar as máquinas de estados

O painel inferior mostra o estado de cada agente em tempo real. As cores identificam Worker (azul), Boss (laranja) e TaxCollector (vinho); as casas usam W, B e T.

Para observar o chefe, aguarde 5 segundos sem trabalhar após a liberação do dia. O deslocamento e a cobrança da manhã não contam como inatividade. Ele entra em ANGRY e o Worker entra em IDLE. Após 0,8 segundo, o chefe volta a WATCHING. Outro período de 5 segundos sem trabalho gera a segunda advertência e, depois da animação de raiva, a perseguição.

Para observar a perseguição dupla, produza pelo menos 41 moedas no primeiro dia. No segundo, o cobrador caminha até o Worker e inicia a perseguição. Como o Worker deixa de trabalhar, o chefe registra suas próprias advertências até começar a perseguir também.

Os logs mostram a ligação entre os eventos e os estados, por exemplo:

```text
[...][DAY 1][EVENT] WORK_PERFORMED | source=WORKER | balance=1 production=1
[...][DAY 1][BOSS] WORK_PERFORMED received | inactivity reset
[...][DAY 1][BOSS] WATCHING -> ANGRY | reason=5 seconds without work
```

## Organização MVC

```text
src/taxandrun/
  Main.java             composição da aplicação e inicialização Swing
  model/
    game/               contexto, configuração, logs e GameSnapshot
    state/              contrato State, StateMachine e base dos estados
    event/              eventos e fila de comunicação
    agent/              Worker, Boss, TaxCollector e seus estados concretos
    world/              posições, deslocamento e rota de fuga
  controller/           GameController, GameLoop e InputHandler
  view/                 janela, painel, desenho, fonte e pixel art
test/taxandrun/    testes executáveis somente com Java padrão
docs/             revisão dos requisitos e evidências de validação
```

| Camada | Responsabilidade | Limite |
| --- | --- | --- |
| Model | Dados, agentes, regras econômicas, estados e eventos | Não importa Swing, AWT, View ou Controller |
| Controller | Receber comandos, avançar a simulação e coordenar fases | As decisões específicas de cada agente continuam em seus estados |
| View | Desenhar mapa, agentes, HUD e botão | Recebe `GameSnapshot` imutável, sem acesso aos agentes mutáveis ou ao Controller |

`Main` conecta as camadas. `InputHandler` converte mouse e teclado em comandos do `GameController`. A View recebe um fornecedor de snapshots; desenhar um quadro não altera saldo, timers nem estados. `ArchitectureTest` verifica essas dependências e o isolamento dos snapshots.

O loop utiliza um Timer Swing e passos de simulação de 1/60 s na EDT. Os eventos entram em uma fila FIFO e chegam a todos os agentes antes do próximo evento. Não há threads de domínio, dependências externas nem lógica de decisão em `paintComponent`.

Quando todos chegam aos postos, os dados diários são preparados e o evento `POSTS_REACHED` chega ao cobrador. Seu estado de chegada encaminha o evento ao novo estado WAITING, que avalia o imposto. A ausência de cobrança, o pagamento ou a fuga geram `TAX_COLLECTION_RESOLVED`. Só depois é emitido `DAY_STARTED`, liberando os relógios e o trabalho quando permitido. O reset diário não se repete nesse momento, preservando o resultado da cobrança.

Durante MORNING e durante o retorno para casa em NIGHT, apenas o relógio das animações avança. A contagem noturna é liberada quando os três agentes estão em casa e dormindo. O HUD mantém as barras cheias nessas esperas e indica TAX COLLECTION ou GOING HOME.

Os perseguidores mantêm um pequeno espaçamento visual para que seus sprites fiquem distinguíveis. Isso não produz captura ou modifica as condições que iniciam e encerram uma perseguição.

## Testes

```powershell
.\test.ps1
# Inclui abertura, atualização e fechamento de uma janela real:
.\test.ps1 -WindowSmoke
```

Se necessário:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\test.ps1 -WindowSmoke
```

Os testes compilam com `--release 17 -Xlint:all -Werror`, sem JUnit ou outras bibliotecas. A verificação realizada nesta versão usou JDK 24 com saída compatível com Java 17.

- **SimulationTest:** 175 verificações sobre os nove cenários, faixas de imposto, bloqueio de trabalho e relógios durante a cobrança, início da noite após a última chegada, ausência de dívida futura, pausa, reset e logs.
- **UiTest:** 21 verificações sobre controles, coordenadas do mouse após redimensionamento, ausência de mutação pela renderização e geração de imagens de conferência.
- **ArchitectureTest:** 53 verificações sobre dependências entre camadas MVC e snapshots imutáveis, que continuam iguais mesmo quando a simulação avança ou reinicia.
- **WindowSmokeTest:** verifica uma janela Swing real, o avanço do loop, a resposta da EDT e a parada do timer ao fechar.

Os testes avançam o relógio da simulação, sem esperar os 40 segundos reais de cada dia. As imagens de manhã, cobrança, dia, advertência, perseguição, retorno para casa, noite e pausa são salvas em `out/`, que fica fora do Git.

A validação automatizada não substitui a revisão manual final dos nove cenários descritos na especificação.
