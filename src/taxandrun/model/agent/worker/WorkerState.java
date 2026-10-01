package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;
import taxandrun.model.game.GamePhase;
import taxandrun.model.state.AgentState;

public abstract class WorkerState extends AgentState<Worker> {
    @Override public void onEvent(Worker owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.NIGHT_STARTED) {
            owner.clearPursuers();
            owner.machine().changeState(new WorkerGoingHomeState(), "night started");
        } else if (context.phase() != GamePhase.NIGHT
                && (event.type() == GameEventType.BOSS_CHASE_STARTED
                || event.type() == GameEventType.TAX_COLLECTOR_CHASE_STARTED)) {
            owner.addPursuer(event.type() == GameEventType.BOSS_CHASE_STARTED
                    ? Worker.ChaseReason.BOSS : Worker.ChaseReason.TAX_COLLECTOR);
            if (!(this instanceof WorkerFleeingState)) {
                owner.machine().changeState(new WorkerFleeingState(), event.type().name());
            }
        }
    }

    @Override public void exit(Worker owner, GameContext context) {
        super.exit(owner, context);
        owner.setWorkEnabled(false);
    }
}
