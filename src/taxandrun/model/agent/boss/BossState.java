package taxandrun.model.agent.boss;

import taxandrun.model.agent.Boss;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;
import taxandrun.model.state.AgentState;

public abstract class BossState extends AgentState<Boss> {
    @Override public void onEvent(Boss owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.NIGHT_STARTED) {
            owner.machine().changeState(new BossGoingHomeState(), "night started");
        } else if (event.type() == GameEventType.WORK_PERFORMED) {
            owner.resetInactivity();
            context.log().write(context.day(), owner.name(), "WORK_PERFORMED received | inactivity reset");
        }
    }
}
