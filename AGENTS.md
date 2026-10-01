# Orientações para Tax & Run

## Escopo e referência

- Leia integralmente `TAX_AND_RUN_IMPLEMENTATION_SPEC.md` antes de implementar ou alterar o jogo.
- O pedido atual do usuário delimita o trabalho. A especificação descreve o produto completo e não autoriza, por si só, iniciar todas as etapas.
- Na preparação inicial, organize somente o repositório e a documentação; a programação será uma etapa posterior.
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
- A camada gráfica lê os dados e desenha; não decide estados, cobra impostos ou altera contadores.
- Use comunicação explícita por eventos entre os agentes, com logs que permitam acompanhar seus efeitos.

## Agentes

- Worker: SLEEPING, GOING_TO_WORK, WORKING, IDLE, FLEEING, GOING_HOME.
- Boss: SLEEPING, GOING_TO_WORK, WATCHING, ANGRY, CHASING, GOING_HOME.
- TaxCollector: SLEEPING, GOING_TO_BANK, WAITING, GOING_TO_COLLECT, COLLECTING, RETURNING_TO_BANK, CHASING, GOING_HOME.

## Regras a preservar

- MORNING: os três agentes saem de casa e se deslocam até seus postos. DAY só começa quando todos chegam.
- DAY dura 40 segundos. NIGHT dura aproximadamente 5 segundos, encerra perseguições e leva todos para casa e SLEEPING.
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
- O saldo persiste entre dias. No novo DAY, a produção anterior recebe a produção encerrada e os contadores e flags diários são reiniciados.
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
