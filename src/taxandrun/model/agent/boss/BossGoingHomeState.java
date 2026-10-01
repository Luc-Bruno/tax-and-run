package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class BossGoingHomeState extends BossState {
    @Override public String getName() { return "GOING_HOME"; }
    @Override public void enter(Boss owner, GameContext context) {
        owner.setTarget(owner.home(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(Boss owner, GameContext context, double dt) {
        if (owner.move(dt)) owner.machine().changeState(new BossSleepingState(), "home reached");
    }
}
