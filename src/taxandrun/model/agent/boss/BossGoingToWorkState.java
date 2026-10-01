package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class BossGoingToWorkState extends BossState {
    @Override public String getName() { return "GOING_TO_WORK"; }
    @Override public void enter(Boss owner, GameContext context) {
        owner.setTarget(owner.post(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(Boss owner, GameContext context, double dt) { owner.move(dt); }
    @Override public void onEvent(Boss owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.DAY_STARTED) {
            owner.machine().changeState(new BossWatchingState(), "morning collection resolved; start watching");
        } else super.onEvent(owner, context, event);
    }
}
