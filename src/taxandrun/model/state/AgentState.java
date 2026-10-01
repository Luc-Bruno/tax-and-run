package taxandrun.model.state;

import taxandrun.model.agent.Agent;
import taxandrun.model.game.GameContext;

public abstract class AgentState<T extends Agent<T>> implements State<T> {
    @Override public void enter(T owner, GameContext context) {}
    @Override public void execute(T owner, GameContext context, double dt) {}
    @Override public void exit(T owner, GameContext context) { owner.clearTarget(); }
}
