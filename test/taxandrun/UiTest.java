package taxandrun;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import taxandrun.model.game.GameConfig;
import taxandrun.controller.GameController;
import taxandrun.model.game.GamePhase;
import taxandrun.view.GamePanel;
import taxandrun.view.GameRenderer;
import taxandrun.controller.InputHandler;

public final class UiTest {
    private static int checks;

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(UiTest::controls);
        previews();
        System.out.println("PASS: " + checks + " UI assertions; previews saved to out.");
    }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void startDay(GameController game) {
        for (int i = 0; i < 400 && game.context().phase() != GamePhase.DAY; i++) {
            game.update(GameConfig.STEP_SECONDS);
        }
        check(game.context().phase() == GamePhase.DAY, "DAY reached");
    }

    private static void action(GamePanel panel, String name) {
        panel.getActionMap().get(name).actionPerformed(new ActionEvent(panel, 0, name));
    }

    private static void controls() {
        GameController game = new GameController(s -> {});
        GamePanel panel = new GamePanel(game::snapshot);
        panel.setSize(1280, 720);
        boolean[] closed = {false};
        new InputHandler(panel, game, () -> closed[0] = true);
        startDay(game);
        action(panel, "work");
        action(panel, "work");
        game.update(0);
        check(game.context().worker().balance() == 1, "SPACE auto-repeat does not act as extra presses");
        action(panel, "workReleased");
        action(panel, "work");
        game.update(0);
        check(game.context().worker().balance() == 2, "Fresh SPACE press works immediately");
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 1, 0,
                275 * 4, 160 * 4, 1, false, MouseEvent.BUTTON1));
        game.update(0);
        check(game.context().worker().balance() == 3, "WORK button click works");
        panel.dispatchEvent(new MouseEvent(panel, MouseEvent.MOUSE_PRESSED, 1, 0,
                275 * 4, 160 * 4, 1, false, MouseEvent.BUTTON3));
        game.update(0);
        check(game.context().worker().balance() == 3, "Right-click does not work");
        action(panel, "pause");
        check(game.paused() && !game.canWork(), "P pauses and disables work");
        action(panel, "reset");
        check(!game.paused() && game.context().worker().balance() == 0, "R resets while paused");
        action(panel, "close");
        check(closed[0], "ESC closes through callback");
        panel.setSize(1400, 900);
        Point point = panel.virtualPoint(60 + 275 * 4, 90 + 160 * 4);
        check(GameRenderer.workButtonContains(point), "Mouse coordinates account for letterboxing");
        check(!GameRenderer.workButtonContains(panel.virtualPoint(0, 0)), "Letterbox cannot trigger work");
    }

    private static void previews() throws IOException {
        GameController game = new GameController(s -> {});
        game.update(0.5);
        render(game, "morning");
        startDay(game);
        for (int i = 0; i < 41; i++) game.requestWork();
        game.update(0);
        render(game, "day");
        game.togglePause();
        render(game, "paused");
        game.togglePause();
        game.update(5);
        render(game, "warning");
        game.update(game.context().remainingSeconds());
        render(game, "going-home");
        game.update(4.8);
        render(game, "night");
        while (!(game.context().day() == 2 && game.awaitingTaxCollection())) {
            game.update(GameConfig.STEP_SECONDS);
        }
        game.update(2);
        check(!game.canWork() && game.context().remainingSeconds() == 40,
                "Collection preview keeps work disabled and clock full");
        render(game, "collection");
        while (!(game.context().day() == 2 && game.context().phase() == GamePhase.DAY)) {
            game.update(GameConfig.STEP_SECONDS);
        }
        game.update(14);
        check(game.context().worker().pursuers().size() == 2, "Preview contains double chase");
        render(game, "chase");
    }

    private static void render(GameController game, String name) throws IOException {
        BufferedImage virtual = new BufferedImage(320, 180, BufferedImage.TYPE_INT_RGB);
        String before = snapshot(game);
        Graphics2D graphics = virtual.createGraphics();
        new GameRenderer().render(graphics, game.snapshot());
        graphics.dispose();
        check(before.equals(snapshot(game)), "Rendering never mutates game: " + name);
        BufferedImage scaled = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_RGB);
        Graphics2D output = scaled.createGraphics();
        output.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        output.drawImage(virtual, 0, 0, 1280, 720, null);
        output.dispose();
        Path path = Path.of("out", "preview-" + name + ".png");
        Files.createDirectories(path.getParent());
        ImageIO.write(scaled, "png", path.toFile());
    }

    private static String snapshot(GameController game) {
        return game.context().phase() + ":" + game.context().elapsed()
                + ":" + game.context().remainingSeconds() + ":" + game.context().countdownRunning()
                + ":" + game.awaitingTaxCollection() + ":" + game.context().boss().inactivityTimer()
                + ":" + game.context().worker().balance() + ":" + game.context().worker().dailyProduction()
                + ":" + game.context().boss().warningCountToday() + ":" + game.context().taxCollector().taxStatus()
                + ":" + game.context().agents().stream().map(a -> a.stateName() + a.position()).toList();
    }
}
