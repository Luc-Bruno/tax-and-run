package taxandrun;

import javax.swing.SwingUtilities;
import taxandrun.controller.GameController;
import taxandrun.controller.GameLoop;
import taxandrun.controller.InputHandler;
import taxandrun.view.GamePanel;
import taxandrun.view.GameWindow;

public final class Main {
    private Main() {}
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameController controller = new GameController();
            GamePanel panel = new GamePanel(controller::snapshot);
            GameLoop loop = new GameLoop(controller, panel::repaint);
            GameWindow window = new GameWindow(panel, loop::start, loop::stop);
            new InputHandler(panel, controller, window::dispose);
            window.open();
        });
    }
}
