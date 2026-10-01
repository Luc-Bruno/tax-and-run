# Orientações para Tax & Run

## Escopo e referência

- Leia integralmente `TAX_AND_RUN_IMPLEMENTATION_SPEC.md` antes de implementar ou alterar o jogo.
- O pedido atual do usuário delimita o trabalho. A especificação descreve o produto completo e não autoriza, por si só, iniciar todas as etapas.
- A versão jogável está implementada em MVC, com interface Swing e cobertura automatizada dos nove cenários. Consulte `docs/VALIDACAO.md` para o alcance da revisão.
- Preserve arquivos existentes. Concentre as alterações nesta pasta, sem modificar os outros trabalhos na pasta superior.
- Pergunte ao usuário quando houver dúvida real ou conflito entre regras; não invente regras de negócio.

## Tecnologia e arquitetura

- Java 17 ou superior, somente Java padrão, Swing, AWT e Java2D.
- Não adicione bibliotecas externas, engines ou dependências externas Maven/Gradle.
- Não migre para JavaFX sem autorização explícita.
- O padrão **State** é requisito acadêmico e não deve ser simplificado ou removido.
- Cada agente deve ter uma FSM própria e estados concretos com `enter`, `execute` e `exit`.
- Não substitua os estados por um `switch` centralizado, enum comportamental ou conjunto de `if/else`.
- Mantenha `StateMachine`, loop principal, domínio, eventos, entrada e renderização separados.
- Preserve a organização MVC solicitada pelo usuário: `model/` para domínio e FSMs, `controller/` para coordenação, loop e entrada, `view/` para apresentação. `Main` compõe as camadas.
- O Model não pode depender de Swing, AWT, Controller ou View.
- A View recebe somente `GameSnapshot` imutável; não recebe `GameContext`, agentes mutáveis ou `GameController`. Entradas são conectadas pelo `InputHandler` no Controller.
- A camada gráfica lê os dados e desenha; não decide estados, cobra impostos ou altera contadores.
- Use comunicação explícita por eventos entre os agentes, com logs que permitam acompanhar seus efeitos.

## Agentes

- Worker: SLEEPING, GOING_TO_WORK, WORKING, IDLE, FLEEING, GOING_HOME.
- Boss: SLEEPING, GOING_TO_WORK, WATCHING, ANGRY, CHASING, GOING_HOME.
- TaxCollector: SLEEPING, GOING_TO_BANK, WAITING, GOING_TO_COLLECT, COLLECTING, RETURNING_TO_BANK, CHASING, GOING_HOME.

## Regras a preservar

- MORNING: os três agentes chegam aos postos; depois o cobrador se desloca até o Worker e resolve a cobrança. DAY só começa após o pagamento ou a fuga. Se não houver imposto, DAY começa assim que todos chegam aos postos.
- Antes de DAY, o trabalho fica bloqueado e os timers do dia e da inatividade do chefe ficam parados. O retorno do cobrador ao banco não bloqueia DAY.
- DAY dura 40 segundos completos após a cobrança. NIGHT encerra as perseguições e leva todos para casa; seus 5 segundos só começam quando todos estão em suas casas e em SLEEPING. O deslocamento noturno não consome esse tempo.
- Cada trabalho válido concede 1 moeda e aumenta a produção diária em 1, sem cooldown obrigatório.
- O trabalho emite WORK_PERFORMED e reinicia o timer de inatividade do Boss.
- Boss registra advertência após 5 segundos sem trabalho e entra em ANGRY por aproximadamente 0,8 segundo.
- Após a primeira advertência, Boss volta a WATCHING; após a segunda, entra em CHASING e provoca a fuga do Worker.
- Trabalhar não zera as advertências; elas zeram somente no início do novo dia.
- O imposto usa a produção do dia anterior: 0 = sem cobrança; 1–20 = 5 moedas; 21–40 = 15 moedas; acima de 40 = fuga quando o cobrador chega.
- O TaxCollector se desloca fisicamente até o Worker antes de cobrar ou provocar fuga.
- Saldo insuficiente provoca fuga. Nunca permita saldo negativo ou acumule dívida para dias futuros.
- Worker não trabalha em FLEEING e percorre uma rota simples de waypoints. Não existe captura; a perseguição termina em NIGHT.
- Perseguição dupla deve ser possível. Boss começa a perseguir por sua própria inatividade, nunca por uma regra que copie a perseguição do TaxCollector.
- O saldo persiste entre dias. Quando todos chegam aos postos pela manhã, antes da cobrança, a produção anterior recebe a produção encerrada e os contadores e flags diários são reiniciados uma única vez. Não reinicie novamente ao liberar DAY, para preservar o resultado da cobrança e a fuga.
- POSTS_REACHED inicia a avaliação do imposto; TAX_COLLECTION_RESOLVED comunica ausência de imposto, pagamento ou fuga. Só então DAY_STARTED libera trabalho e monitoramento do chefe.
- No primeiro dia, a produção anterior é zero e não há imposto.

## Interface, logs e validação

- HUD com fase, dia, timer, saldo, produção atual/anterior, advertências, situação do imposto e estados dos três agentes.
- Botão WORK! disponível quando o Worker pode trabalhar.
- Visual pixelizado com fallback programático caso não existam assets externos.
- Logs de entrada, saída, transição, motivo, eventos e operações econômicas; não imprima a cada frame.
- Ao implementar, valide os nove cenários da especificação, incluindo perseguição dupla e encerramento noturno.
- Não gere PDF: esse entregável foi substituído pela interface conforme a especificação.

## Mudanças

- Confirme com o usuário alterações de tempos, impostos, advertências, agentes, perseguições, arquitetura ou tecnologia de interface.
- Atualize `README.md` e `AGENTS.md` quando uma regra aprovada mudar.
- Documente somente funcionalidades e comandos realmente disponíveis; identifique claramente o que ainda está planejado.

## Desenvolvimento e validação

- Código em `src/taxandrun/`; testes sem bibliotecas externas em `test/taxandrun/`.
- Use `build.ps1` para compilar com `--release 17`, `run.ps1` para executar e `test.ps1` para validar.
- Os scripts aceitam `-JavaHome` e procuram JDK 17+ em JAVA_HOME, PATH e na pasta pessoal `.jdks`, sem alterar o sistema.
- `test.ps1 -WindowSmoke` inclui abertura e fechamento de uma janela real para verificar o loop Swing.
- Os testes automatizados cobrem os nove cenários da especificação. Não os descreva como testes manuais.
- `ArchitectureTest` verifica dependências MVC e isolamento dos snapshots. Execute-o junto aos testes de simulação e interface ao alterar a arquitetura.
- Imagens de conferência visual são geradas em `out/` e não entram no Git.
- A renderização deve continuar funcionando sem assets externos ou fontes instaladas.
- A distância visual entre perseguidores serve somente à legibilidade; não representa captura, dano ou uma nova regra de perseguição.
