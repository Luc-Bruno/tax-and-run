package taxandrun.model.event;

public record GameEvent(GameEventType type, String source, String detail, int value) {
    public GameEvent(GameEventType type, String source, String detail) {
        this(type, source, detail, 0);
    }
}
