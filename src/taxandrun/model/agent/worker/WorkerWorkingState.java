package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class WorkerWorkingState extends WorkerState {
    @Override public String getName() { return "WORKING"; }
    @Override public void enter(Worker owner, GameContext context) { owner.setWorkEnabled(true); }
    @Override public void onEvent(Worker owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.WORK_REQUESTED) owner.performWork(context);
        else if (event.type() == GameEventType.BOSS_WARNING && event.value() == 1) {
            owner.machine().changeState(new WorkerIdleState(), "boss first warning");
        } else super.onEvent(owner, context, event);
    }
}
