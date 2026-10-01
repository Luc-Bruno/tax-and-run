package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.game.GameContext;
import taxandrun.model.game.GamePhase;

public final class BossSleepingState extends BossState {
    @Override public String getName() { return "SLEEPING"; }
    @Override public void enter(Boss owner, GameContext context) { owner.sleepAtHome(); }
    @Override public void execute(Boss owner, GameContext context, double dt) {
        if (context.phase() == GamePhase.MORNING) {
            owner.machine().changeState(new BossGoingToWorkState(), "morning started");
        }
    }
    @Override public void exit(Boss owner, GameContext context) {
        super.exit(owner, context);
        owner.wakeUp();
    }
}
