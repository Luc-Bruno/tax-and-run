package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class TaxChasingState extends TaxState {
    private final String reason;
    public TaxChasingState(String reason) { this.reason = reason; }
    @Override public String getName() { return "CHASING"; }
    @Override public void enter(TaxCollector owner, GameContext context) {
        context.events().publish(new GameEvent(GameEventType.TAX_COLLECTOR_CHASE_STARTED,
                owner.name(), reason));
    }
    @Override public void execute(TaxCollector owner, GameContext context, double dt) {
        owner.follow(context.worker().position(), GameConfig.CHASE_SPEED, 22, dt);
    }
}
