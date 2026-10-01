package taxandrun;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;
import taxandrun.controller.GameController;
import taxandrun.model.game.GameSnapshot;

public final class ArchitectureTest {
    private static int checks;

    public static void main(String[] args) throws IOException {
        verifyDependencies(Path.of("src/taxandrun/model"),
                Pattern.compile("taxandrun\\.(controller|view)\\b|javax\\.swing\\b|java\\.awt\\b"));
        verifyDependencies(Path.of("src/taxandrun/view"),
                Pattern.compile("taxandrun\\.controller\\b|taxandrun\\.model\\.(agent|state|event)\\b|\\bGameContext\\b"));
        detachedSnapshot();
        System.out.println("PASS: " + checks + " architecture assertions; MVC boundaries and immutable view data verified.");
    }

    private static void verifyDependencies(Path root, Pattern forbidden) throws IOException {
        try (var files = Files.walk(root)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".java")).toList()) {
                check(!forbidden.matcher(Files.readString(file)).find(), "Forbidden dependency in " + file);
            }
        }
    }

    private static void detachedSnapshot() {
        GameController game = new GameController(line -> {});
        for (int i = 0; i < 300 && !game.canWork(); i++) game.update(1.0 / 60);
        GameSnapshot before = game.snapshot();
        check(before.canWork(), "Snapshot reflects enabled work");
        game.requestWork();
        game.update(0);
        check(before.balance() == 0, "Existing snapshot cannot change with live balance");
        check(game.snapshot().balance() == 1, "New snapshot reflects latest balance");
        boolean immutableList = false;
        try {
            before.agents().clear();
        } catch (UnsupportedOperationException expected) {
            immutableList = true;
        }
        check(immutableList, "View cannot replace agents through snapshot list");
        var position = before.worker().position();
        game.update(12);
        check(before.worker().position().equals(position), "Snapshot position stays detached during pursuit");
        check(before.worker().stateName().equals("WORKING"), "Snapshot state stays detached during pursuit");
        check(game.snapshot().worker().stateName().equals("FLEEING"), "Live model can transition independently");
        game.reset();
        check(game.snapshot().balance() == 0 && game.snapshot().day() == 1, "Reset produces fresh snapshot data");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }
}
