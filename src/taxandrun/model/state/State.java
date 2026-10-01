package taxandrun.model.state;

import taxandrun.model.event.GameEvent;
import taxandrun.model.game.GameContext;

public interface State<T> {
    void enter(T owner, GameContext context);
    void execute(T owner, GameContext context, double deltaTime);
    void exit(T owner, GameContext context);
    default void onEvent(T owner, GameContext context, GameEvent event) {}
    String getName();
}
