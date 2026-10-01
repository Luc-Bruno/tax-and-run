package taxandrun.model.state;

import java.util.Objects;
import taxandrun.model.event.GameEvent;
import taxandrun.model.game.GameContext;

public final class StateMachine<T> {
    private final T owner;
    private final String ownerName;
    private final GameContext context;
    private State<T> current;

    public StateMachine(T owner, String ownerName, GameContext context) {
        this.owner = owner;
        this.ownerName = ownerName;
        this.context = context;
    }

    public void changeState(State<T> next, String reason) {
        Objects.requireNonNull(next);
        String previous = current == null ? "NONE" : current.getName();
        if (current != null) {
            context.log().write(context.day(), ownerName, "EXIT " + previous);
            current.exit(owner, context);
        }
        current = next;
        context.log().write(context.day(), ownerName,
                previous + " -> " + next.getName() + " | reason=" + reason);
        context.log().write(context.day(), ownerName, "ENTER " + next.getName());
        current.enter(owner, context);
    }

    public void update(double dt) { current.execute(owner, context, dt); }
    public void onEvent(GameEvent event) { current.onEvent(owner, context, event); }
    public String name() { return current.getName(); }
}
