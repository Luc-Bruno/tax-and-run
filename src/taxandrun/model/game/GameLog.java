package taxandrun.model.game;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public final class GameLog {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    private final Consumer<String> output;

    public GameLog(Consumer<String> output) { this.output = output; }

    public void write(int day, String source, String message) {
        output.accept("[" + LocalTime.now().format(TIME) + "][DAY " + day
                + "][" + source + "] " + message);
    }
}
