package taxandrun;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import taxandrun.controller.GameController;
import taxandrun.controller.GameLoop;
import taxandrun.controller.InputHandler;
import taxandrun.view.GamePanel;
import taxandrun.view.GameWindow;

public final class WindowSmokeTest {
    public static void main(String[] args) throws Exception {
        CountDownLatch completed = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        AtomicReference<GameWindow> windowRef = new AtomicReference<>();
        AtomicReference<GameLoop> loopRef = new AtomicReference<>();
        SwingUtilities.invokeAndWait(() -> {
            GameController controller = new GameController(line -> {});
            GamePanel panel = new GamePanel(controller::snapshot);
            GameLoop loop = new GameLoop(controller, panel::repaint);
            loopRef.set(loop);
            GameWindow window = new GameWindow(panel, loop::start, loop::stop);
            new InputHandler(panel, controller, window::dispose);
            windowRef.set(window);
            window.open();
            Timer finish = new Timer(500, event -> {
                try {
                    if (!window.isShowing()) throw new AssertionError("Window not visible");
                    if (controller.context().elapsed() <= 0) throw new AssertionError("GameLoop not updating");
                    if (!loop.running()) throw new AssertionError("Swing timer not running");
                } catch (Throwable failure) {
                    error.set(failure);
                } finally {
                    window.dispose();
                    completed.countDown();
                }
            });
            finish.setRepeats(false);
            finish.start();
        });
        if (!completed.await(10, TimeUnit.SECONDS)) {
            SwingUtilities.invokeAndWait(() -> windowRef.get().dispose());
            throw new AssertionError("Swing event thread did not respond");
        }
        SwingUtilities.invokeAndWait(() -> {
            if (loopRef.get().running()) {
                error.set(new AssertionError("GameLoop did not stop on close"));
            }
        });
        if (error.get() != null) throw new AssertionError("Window smoke test failed", error.get());
        System.out.println("PASS: Swing window opened, loop advanced, EDT responded, window closed and timer stopped.");
    }
}
