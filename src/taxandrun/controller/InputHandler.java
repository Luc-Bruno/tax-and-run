package taxandrun.controller;

import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import taxandrun.view.GamePanel;

public final class InputHandler {
    private boolean spaceDown;

    public InputHandler(GamePanel panel, GameController controller, Runnable close) {
        panel.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent event) {
                if (SwingUtilities.isLeftMouseButton(event)
                        && panel.isWorkButtonAt(event.getX(), event.getY())) {
                    controller.requestWork();
                }
                panel.requestFocusInWindow();
            }
        });
        panel.addFocusListener(new FocusAdapter() {
            @Override public void focusLost(FocusEvent event) { spaceDown = false; }
        });
        bind(panel, "pressed SPACE", "work", () -> {
            if (!spaceDown) controller.requestWork();
            spaceDown = true;
        });
        bind(panel, "released SPACE", "workReleased", () -> spaceDown = false);
        bind(panel, "pressed P", "pause", controller::togglePause);
        bind(panel, "pressed R", "reset", () -> { spaceDown = false; controller.reset(); });
        bind(panel, "pressed ESCAPE", "close", close);
    }

    private static void bind(JComponent component, String key, String action, Runnable handler) {
        component.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), action);
        component.getActionMap().put(action, new AbstractAction() {
            @Override public void actionPerformed(ActionEvent event) { handler.run(); }
        });
    }
}
