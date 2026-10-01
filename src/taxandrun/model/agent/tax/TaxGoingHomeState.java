package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class TaxGoingHomeState extends TaxState {
    @Override public String getName() { return "GOING_HOME"; }
    @Override public void enter(TaxCollector owner, GameContext context) {
        owner.setTarget(owner.home(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        if (owner.move(dt)) owner.machine().changeState(new TaxSleepingState(), "home reached");
    }
}
