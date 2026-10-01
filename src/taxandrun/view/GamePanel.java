package taxandrun.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import javax.swing.JPanel;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameSnapshot;

@SuppressWarnings("serial")
public final class GamePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private final BufferedImage canvas = new BufferedImage(GameConfig.VIRTUAL_WIDTH,
            GameConfig.VIRTUAL_HEIGHT, BufferedImage.TYPE_INT_RGB);
    private final GameRenderer renderer = new GameRenderer();
    private final Supplier<GameSnapshot> snapshots;

    public GamePanel(Supplier<GameSnapshot> snapshots) {
        this.snapshots = snapshots;
        setPreferredSize(new Dimension(1280, 720));
        setBackground(new Color(0x0F1F2C));
        setFocusable(true);
    }

    @Override protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D virtualGraphics = canvas.createGraphics();
        renderer.render(virtualGraphics, snapshots.get());
        virtualGraphics.dispose();
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        int scale = scale();
        g.drawImage(canvas, offsetX(scale), offsetY(scale),
                canvas.getWidth() * scale, canvas.getHeight() * scale, null);
        g.dispose();
    }

    public Point virtualPoint(int x, int y) {
        int scale = scale();
        return new Point((int) Math.floor((x - offsetX(scale)) / (double) scale),
                (int) Math.floor((y - offsetY(scale)) / (double) scale));
    }

    public boolean isWorkButtonAt(int x, int y) {
        return GameRenderer.workButtonContains(virtualPoint(x, y));
    }

    private int scale() {
        return Math.max(1, Math.min(getWidth() / canvas.getWidth(), getHeight() / canvas.getHeight()));
    }
    private int offsetX(int scale) { return (getWidth() - canvas.getWidth() * scale) / 2; }
    private int offsetY(int scale) { return (getHeight() - canvas.getHeight() * scale) / 2; }
}
