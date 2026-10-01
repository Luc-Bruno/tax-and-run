package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class WorkerGoingHomeState extends WorkerState {
    @Override public String getName() { return "GOING_HOME"; }
    @Override public void enter(Worker owner, GameContext context) {
        owner.setTarget(owner.home(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(Worker owner, GameContext context, double dt) {
        if (owner.move(dt)) owner.machine().changeState(new WorkerSleepingState(), "home reached");
    }
}
