package taxandrun.controller;

import javax.swing.Timer;
import taxandrun.model.game.GameConfig;

public final class GameLoop {
    private final GameController controller;
    private final Runnable repaint;
    private final Timer timer;
    private long previousTime;
    private double accumulator;

    public GameLoop(GameController controller, Runnable repaint) {
        this.controller = controller;
        this.repaint = repaint;
        timer = new Timer(16, event -> tick());
        timer.setCoalesce(true);
    }

    public void start() {
        previousTime = System.nanoTime();
        accumulator = 0;
        timer.start();
    }

    private void tick() {
        long now = System.nanoTime();
        // Uma suspensão da janela não deve produzir minutos de catch-up na EDT.
        accumulator += Math.min(0.25, (now - previousTime) / 1_000_000_000.0);
        previousTime = now;
        while (accumulator >= GameConfig.STEP_SECONDS) {
            controller.update(GameConfig.STEP_SECONDS);
            accumulator -= GameConfig.STEP_SECONDS;
        }
        repaint.run();
    }

    public void stop() { timer.stop(); }
    public boolean running() { return timer.isRunning(); }
}
