package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class TaxCollectingState extends TaxState {
    @Override public String getName() { return "COLLECTING"; }
    @Override public void enter(TaxCollector owner, GameContext context) {
        owner.showFeedback(context.worker().previousDayProduction() > 40 ? "!" : "$" + owner.taxDue());
    }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        Worker worker = context.worker();
        if (worker.previousDayProduction() > 40 || worker.balance() < owner.taxDue()) {
            String reason = worker.previousDayProduction() > 40 ? "HIGH_PRODUCTION" : "INSUFFICIENT_FUNDS";
            owner.setTaxStatus("EVASION");
            context.events().publish(new GameEvent(GameEventType.TAX_EVASION_STARTED, owner.name(), reason));
            owner.machine().changeState(new TaxChasingState(reason), reason);
        } else {
            worker.pay(owner.taxDue());
            owner.setTaxStatus("PAID $" + owner.taxDue());
            context.log().write(context.day(), "TAX", "payment successful balanceAfter=" + worker.balance());
            context.events().publish(new GameEvent(GameEventType.TAX_PAYMENT_SUCCEEDED, owner.name(),
                    "amount=" + owner.taxDue() + " balance=" + worker.balance(), owner.taxDue()));
            owner.machine().changeState(new TaxReturningToBankState(), "payment completed");
        }
        context.events().publish(new GameEvent(GameEventType.TAX_COLLECTION_RESOLVED,
                owner.name(), owner.taxStatus()));
    }
}
