package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class TaxWaitingState extends TaxState {
    @Override public String getName() { return "WAITING"; }
    @Override public void onEvent(TaxCollector owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.POSTS_REACHED) {
            int previous = context.worker().previousDayProduction();
            boolean collect = owner.assess(previous);
            context.log().write(context.day(), "TAX", "previousProduction=" + previous
                    + " due=" + owner.taxDue() + " balanceBefore=" + context.worker().balance());
            if (collect) {
                context.events().publish(new GameEvent(GameEventType.TAX_COLLECTION_REQUESTED,
                        owner.name(), owner.taxStatus(), owner.taxDue()));
                owner.machine().changeState(new TaxGoingToCollectState(), "previous day has production");
            } else {
                context.events().publish(new GameEvent(GameEventType.TAX_COLLECTION_RESOLVED,
                        owner.name(), "no tax due"));
            }
        } else super.onEvent(owner, context, event);
    }
}
