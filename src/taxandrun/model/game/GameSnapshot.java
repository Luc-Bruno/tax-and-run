package taxandrun.model.game;

import java.util.List;
import taxandrun.model.agent.Agent;
import taxandrun.model.world.Vector2;

/** Dados imutáveis para desenhar um quadro, sem acesso aos agentes ou à máquina de estados. */
public record GameSnapshot(
        int day, GamePhase phase, double elapsed, double remainingSeconds,
        boolean countdownRunning, boolean paused, boolean canWork, boolean awaitingTaxCollection,
        int balance, int dailyProduction, int previousDayProduction, int warningCountToday,
        String taxStatus, AgentSnapshot worker, AgentSnapshot boss, AgentSnapshot taxCollector) {

    public static GameSnapshot capture(GameContext context, boolean paused,
            boolean canWork, boolean awaitingTaxCollection) {
        return new GameSnapshot(context.day(), context.phase(), context.elapsed(),
                context.remainingSeconds(), context.countdownRunning(), paused, canWork,
                awaitingTaxCollection, context.worker().balance(), context.worker().dailyProduction(),
                context.worker().previousDayProduction(), context.boss().warningCountToday(),
                context.taxCollector().taxStatus(), AgentSnapshot.capture(context.worker()),
                AgentSnapshot.capture(context.boss()), AgentSnapshot.capture(context.taxCollector()));
    }

    public List<AgentSnapshot> agents() { return List.of(worker, boss, taxCollector); }

    public record AgentSnapshot(String name, Vector2 position, String stateName,
            boolean sleeping, boolean moving, String feedback, double jumpRemaining) {
        private static AgentSnapshot capture(Agent<?> agent) {
            return new AgentSnapshot(agent.name(), agent.position(), agent.stateName(),
                    agent.sleeping(), agent.moving(), agent.feedback(), agent.jumpRemaining());
        }
    }
}
