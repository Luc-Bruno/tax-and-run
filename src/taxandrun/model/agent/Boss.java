package taxandrun.model.agent;

import taxandrun.model.agent.boss.BossSleepingState;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class Boss extends Agent<Boss> {
    private double inactivityTimer;
    private int warningCountToday;

    public Boss(GameContext context) {
        super("BOSS", context.layout().bossHome, context.layout().bossPost);
        initialize(this, new BossSleepingState(), context);
    }

    public void prepareDay() { inactivityTimer = 0; warningCountToday = 0; }
    public void resetInactivity() { inactivityTimer = 0; }
    public void countInactivity(double dt) { inactivityTimer += dt; }
    public void warn(GameContext context) {
        inactivityTimer = 0;
        warningCountToday++;
        context.events().publish(new GameEvent(GameEventType.BOSS_WARNING, name(),
                "count=" + warningCountToday, warningCountToday));
    }
    public double inactivityTimer() { return inactivityTimer; }
    public int warningCountToday() { return warningCountToday; }
}
