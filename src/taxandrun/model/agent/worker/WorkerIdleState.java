package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class WorkerIdleState extends WorkerState {
    @Override public String getName() { return "IDLE"; }
    @Override public void enter(Worker owner, GameContext context) { owner.setWorkEnabled(true); }
    @Override public void onEvent(Worker owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.WORK_REQUESTED) {
            owner.machine().changeState(new WorkerWorkingState(), "player resumed work");
            owner.performWork(context);
        } else super.onEvent(owner, context, event);
    }
}
