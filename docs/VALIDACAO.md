# Revisão de Tax & Run

Revisão em 01/10/2026, baseada na especificação fornecida e nos ajustes posteriores do usuário: relógios liberados após as animações e organização MVC. Nenhuma pendência funcional foi identificada nesse escopo. A rubrica original da professora não foi fornecida separadamente; os requisitos acadêmicos conferidos são os transcritos na especificação.

## Requisitos e evidências

| Requisito | Implementação / evidência |
| --- | --- |
| Java 17+, somente biblioteca padrão | Compilação com `--release 17 -Xlint:all -Werror`; nenhum gerenciador ou dependência externa |
| Pelo menos dois agentes e estados próprios | Worker com 6 estados, Boss com 6 e TaxCollector com 8, em `src/taxandrun/model/agent/` |
| Padrão State e ciclo enter/execute/exit | `model/state/State.java`, `StateMachine.java` e classes concretas de cada agente |
| MVC e renderização sem decisões de IA | Pacotes `model`, `controller`, `view`; View lê `GameSnapshot` imutável; `ArchitectureTest` |
| Loop separado e comunicação explícita | `controller/GameLoop.java` e `model/event/GameEventBus.java`, com fila FIFO |
| Transições determinísticas e logs | Timers, contadores e eventos nos estados; `GameLog` registra entradas, saídas, motivos e operações econômicas |
| Manhã aguarda postos e cobrança | `GameController` só emite `DAY_STARTED` após `TAX_COLLECTION_RESOLVED`; testes de trabalho e inatividade bloqueados |
| Dia de 40 segundos e noite com 5 segundos completos de sono | `GameConfig`, `GameContext`, `GameController`; testes verificam a última chegada antes do início do contador noturno |
| Trabalho, advertências, impostos e perseguições | Nove cenários e limites cobertos por `SimulationTest` |
| Comportamento emergente | Perseguição dupla resulta de eventos do cobrador e da inatividade monitorada independentemente pelo chefe |
| Interface funcional e pixel art | Mapa, banco, loja, três casas, HUD, estados, botão, animações e feedback; `UiTest` e janela real |
| Documentação e repositório | README, AGENTS, especificação atualizada e `.gitignore` para bytecode, logs e imagens de teste |

O PDF e os diagramas não são obrigatórios nesta versão, conforme a exceção registrada na seção 1.2 da especificação. Áudio e arquivos de arte externos também não são requisitos pendentes. Por decisão do usuário em 01/10/2026, JavaFX foi descartado: o projeto permanecerá em Java padrão, Swing, AWT e Java2D, sem bibliotecas externas.

## Verificação executada

Ambiente: Windows, JDK 24.0.2, compilando para Java 17. Não houve execução em uma instalação separada do JDK 17.

Comando, na raiz do projeto:

```powershell
powershell.exe -NoProfile -ExecutionPolicy Bypass -File .\test.ps1 -WindowSmoke
```

Resultado:

```text
PASS: 175 assertions; all nine specification scenarios covered.
PASS: 21 UI assertions; previews saved to out.
PASS: 53 architecture assertions; MVC boundaries and immutable view data verified.
PASS: Swing window opened, loop advanced, EDT responded, window closed and timer stopped.
```

A compilação limpa remove bytecode antigo antes de compilar. As 249 verificações passaram sem erros ou avisos de compilação. O teste da janela confirmou que o loop avança, a interface responde e o timer para ao fechar. As imagens geradas e a janela interativa foram inspecionadas visualmente.

| Cenário da especificação | Teste automatizado |
| --- | --- |
| 1. Trabalho normal | `normalWork` |
| 2. Primeira advertência | `warnings` |
| 3. Segunda advertência e perseguição | `warnings` |
| 4. Imposto de 5 moedas | `payment(10, 5)` |
| 5. Imposto de 15 moedas | `payment(25, 15)` |
| 6. Saldo insuficiente | `insufficientFunds` |
| 7. Produção acima de 40 | `evasionAndDoubleChase` |
| 8. Perseguição dupla | `evasionAndDoubleChase` |
| 9. Noite, sono e novo dia | `nightAndReset` |

Além disso, há verificações das fronteiras tributárias, ausência de dívida futura, pausa, reset, entrada do jogador, logs, redimensionamento e separação MVC.

## Alcance da conferência visual

Foram conferidas imagens da manhã, dia, cobrança, advertência, perseguição, retorno para casa, noite e pausa, além da janela Swing em execução. Isso complementa os testes, mas não equivale a uma sessão manual integral dos nove cenários. O roteiro manual da seção 29 da especificação permanece disponível para o ensaio da apresentação.
