package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class BossChasingState extends BossState {
    @Override public String getName() { return "CHASING"; }
    @Override public void enter(Boss owner, GameContext context) {
        context.events().publish(new GameEvent(GameEventType.BOSS_CHASE_STARTED, owner.name(),
                "two inactivity warnings"));
    }
    @Override public void execute(Boss owner, GameContext context, double dt) {
        owner.follow(context.worker().position(), GameConfig.CHASE_SPEED, 12, dt);
    }
}
