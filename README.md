# Tax & Run

Projeto acadêmico da disciplina **Inteligência Artificial e Ilusão de Inteligência em Jogos**: um jogo 2D que demonstra como máquinas de estados e comunicação entre agentes produzem comportamentos aparentemente inteligentes.

## Situação do projeto

Repositório em preparação. Esta primeira etapa organiza a especificação e as orientações de desenvolvimento; o jogo ainda não foi implementado.

A especificação completa está em [TAX_AND_RUN_IMPLEMENTATION_SPEC.md](TAX_AND_RUN_IMPLEMENTATION_SPEC.md). As orientações para trabalhar no código estão em [AGENTS.md](AGENTS.md).

## Proposta

O jogador trabalha por meio do botão `WORK!`, recebendo uma moeda por ação válida. Três agentes interagem por regras determinísticas:

- **Worker:** trabalha, fica ocioso e foge quando começa uma perseguição.
- **Boss:** monitora a inatividade, dá advertências e persegue após a segunda advertência do dia.
- **TaxCollector:** cobra o imposto calculado sobre a produção do dia anterior e persegue em caso de fuga.

Cada agente terá sua própria máquina de estados, implementada com o padrão **State** e os métodos `enter`, `execute` e `exit`.

## Estados previstos

| Agente | Estados |
| --- | --- |
| Worker | SLEEPING, GOING_TO_WORK, WORKING, IDLE, FLEEING, GOING_HOME |
| Boss | SLEEPING, GOING_TO_WORK, WATCHING, ANGRY, CHASING, GOING_HOME |
| TaxCollector | SLEEPING, GOING_TO_BANK, WAITING, GOING_TO_COLLECT, COLLECTING, RETURNING_TO_BANK, CHASING, GOING_HOME |

## Regras principais

- Ciclo global: `MORNING` → `DAY` → `NIGHT`.
- O dia começa quando todos chegam aos postos e dura 40 segundos; a noite dura aproximadamente 5 segundos.
- Cada ação válida de trabalho aumenta o saldo e a produção diária em 1.
- O chefe reage após 5 segundos sem trabalho. Trabalhar reinicia o timer, mas as advertências só zeram no próximo dia.
- Imposto sobre a produção anterior: 0 → sem cobrança; 1–20 → 5 moedas; 21–40 → 15 moedas; acima de 40 → fuga quando o cobrador chega.
- Saldo insuficiente também provoca fuga. Não existe saldo negativo nem dívida acumulada entre dias.
- Perseguições duram até a noite, sem captura. Chefe e cobrador podem perseguir ao mesmo tempo, cada um por sua própria regra.

## Tecnologia prevista

**Java 17 ou superior**, usando somente Java padrão, Swing, AWT e Java2D. Nenhuma biblioteca externa.

A interface mostrará os estados atuais dos agentes, o tempo, o saldo, a produção e as advertências. Logs do console registrarão transições, motivos e comunicação entre agentes. A renderização ficará separada da lógica do jogo.

## Compilação, execução e controles

Os comandos de compilação e execução serão acrescentados quando existir uma versão executável. A ação principal prevista é `WORK!`; atalhos e controles auxiliares serão documentados conforme forem implementados.
