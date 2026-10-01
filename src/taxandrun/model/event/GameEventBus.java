package taxandrun.model.event;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import taxandrun.model.game.GameLog;

public final class GameEventBus {
    private final Queue<GameEvent> pending = new ArrayDeque<>();
    private final List<Consumer<GameEvent>> listeners = new ArrayList<>();
    private final GameLog log;
    private final IntSupplier day;

    public GameEventBus(GameLog log, IntSupplier day) {
        this.log = log;
        this.day = day;
    }

    public void subscribe(Consumer<GameEvent> listener) { listeners.add(listener); }

    public void publish(GameEvent event) {
        pending.add(event);
        log.write(day.getAsInt(), "EVENT", event.type() + " | source="
                + event.source() + " | " + event.detail());
    }

    // FIFO: eventos produzidos por um listener entram no fim da fila.
    // Todos recebem um evento antes de qualquer listener receber o próximo.
    public void dispatchPending() {
        while (!pending.isEmpty()) {
            GameEvent event = pending.remove();
            for (Consumer<GameEvent> listener : listeners) listener.accept(event);
        }
    }
}
