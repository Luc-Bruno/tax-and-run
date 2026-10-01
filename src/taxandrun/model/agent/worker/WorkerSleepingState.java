package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.game.GameContext;
import taxandrun.model.game.GamePhase;

public final class WorkerSleepingState extends WorkerState {
    @Override public String getName() { return "SLEEPING"; }
    @Override public void enter(Worker owner, GameContext context) { owner.sleepAtHome(); }
    @Override public void execute(Worker owner, GameContext context, double dt) {
        if (context.phase() == GamePhase.MORNING) {
            owner.machine().changeState(new WorkerGoingToWorkState(), "morning started");
        }
    }
    @Override public void exit(Worker owner, GameContext context) {
        super.exit(owner, context);
        owner.wakeUp();
    }
}
