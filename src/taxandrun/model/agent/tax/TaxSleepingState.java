package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.game.GameContext;
import taxandrun.model.game.GamePhase;

public final class TaxSleepingState extends TaxState {
    @Override public String getName() { return "SLEEPING"; }
    @Override public void enter(TaxCollector owner, GameContext context) { owner.sleepAtHome(); }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        if (context.phase() == GamePhase.MORNING) {
            owner.machine().changeState(new TaxGoingToBankState(), "morning started");
        }
    }
    @Override public void exit(TaxCollector owner, GameContext context) {
        super.exit(owner, context);
        owner.wakeUp();
    }
}
