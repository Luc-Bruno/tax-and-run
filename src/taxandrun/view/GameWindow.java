package taxandrun.view;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public final class GameWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private final GamePanel panel;
    private final Runnable onOpen;

    public GameWindow(GamePanel panel, Runnable onOpen, Runnable onClose) {
        super("Tax & Run");
        this.panel = panel;
        this.onOpen = onOpen;
        setContentPane(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack();
        setMinimumSize(new Dimension(656, 399));
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent event) { onClose.run(); }
        });
    }

    public void open() {
        setVisible(true);
        panel.requestFocusInWindow();
        onOpen.run();
    }

    public GamePanel gamePanel() { return panel; }
}
