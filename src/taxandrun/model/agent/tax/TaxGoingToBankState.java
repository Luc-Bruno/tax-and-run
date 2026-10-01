package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class TaxGoingToBankState extends TaxState {
    @Override public String getName() { return "GOING_TO_BANK"; }
    @Override public void enter(TaxCollector owner, GameContext context) {
        owner.setTarget(owner.post(), GameConfig.WALK_SPEED);
    }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) { owner.move(dt); }
    @Override public void onEvent(TaxCollector owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.POSTS_REACHED) {
            owner.machine().changeState(new TaxWaitingState(), "all agents at their posts");
            // Entrega a chegada aos postos ao estado WAITING recém-ativado.
            owner.machine().onEvent(event);
        } else super.onEvent(owner, context, event);
    }
}
