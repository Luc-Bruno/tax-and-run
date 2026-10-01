package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class BossAngryState extends BossState {
    private double elapsed;
    @Override public String getName() { return "ANGRY"; }
    @Override public void enter(Boss owner, GameContext context) {
        elapsed = 0;
        owner.jump();
        owner.showFeedback("!");
    }
    @Override public void execute(Boss owner, GameContext context, double dt) {
        elapsed += dt;
        if (elapsed + 1e-9 < GameConfig.ANGER_SECONDS) return;
        if (owner.warningCountToday() >= 2) {
            owner.machine().changeState(new BossChasingState(), "second warning completed");
        } else {
            owner.machine().changeState(new BossWatchingState(), "first warning completed");
        }
    }
}
