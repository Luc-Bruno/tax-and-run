package taxandrun.model.agent.tax;

import taxandrun.model.agent.TaxCollector;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;
import taxandrun.model.state.AgentState;

public abstract class TaxState extends AgentState<TaxCollector> {
    @Override public void onEvent(TaxCollector owner, GameContext context, GameEvent event) {
        if (event.type() == GameEventType.NIGHT_STARTED) {
            owner.setTaxStatus("CLOSED");
            owner.machine().changeState(new TaxGoingHomeState(), "night started; collection closed");
        }
    }
}
