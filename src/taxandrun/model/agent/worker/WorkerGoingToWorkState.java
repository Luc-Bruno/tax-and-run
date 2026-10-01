package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class WorkerGoingToWorkState extends WorkerState {
    @Override public String getName() { return "GOING_TO_WORK"; }
    @Override public void enter(Worker owner, GameContext context) {
        owner.setTarget(owner.post(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(Worker owner, GameContext context, double dt) { owner.move(dt); }
    @Override public void onEvent(Worker owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.DAY_STARTED) {
            owner.machine().changeState(new WorkerWorkingState(), "morning collection resolved");
        } else super.onEvent(owner, context, event);
    }
}
