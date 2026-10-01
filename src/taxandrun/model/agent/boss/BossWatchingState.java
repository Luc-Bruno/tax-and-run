package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class BossWatchingState extends BossState {
    @Override public String getName() { return "WATCHING"; }
    @Override public void execute(Boss owner, GameContext context, double dt) {
        owner.countInactivity(dt);
        if (owner.inactivityTimer() + 1e-9 >= GameConfig.INACTIVITY_SECONDS) {
            owner.warn(context);
            owner.machine().changeState(new BossAngryState(), "5 seconds without work");
        }
    }
}
