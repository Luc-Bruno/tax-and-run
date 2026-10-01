package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class TaxGoingToCollectState extends TaxState {
    @Override public String getName() { return "GOING_TO_COLLECT"; }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        owner.setTarget(context.worker().position(), GameConfig.WALK_SPEED);
        owner.move(dt);
        if (owner.position().distanceTo(context.worker().position()) <= GameConfig.COLLECTION_DISTANCE) {
            owner.machine().changeState(new TaxCollectingState(), "worker reached");
        }
    }
}
