package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class TaxReturningToBankState extends TaxState {
    @Override public String getName() { return "RETURNING_TO_BANK"; }
    @Override public void enter(TaxCollector owner, GameContext context) {
        owner.setTarget(owner.post(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        if (owner.move(dt)) owner.machine().changeState(new TaxWaitingState(), "bank reached");
    }
}
