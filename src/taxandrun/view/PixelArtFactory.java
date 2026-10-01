package taxandrun.view;

import java.awt.Color;
import java.awt.Graphics2D;

public final class PixelArtFactory {
    private PixelArtFactory() {}

    public static void building(Graphics2D g, int x, int y, int width, int height,
            Color wall, Color roof, String label) {
        g.setColor(new Color(0x536B58));
        g.fillRect(x + 3, y + height - 1, width, 3);
        g.setColor(new Color(0x3B414C));
        g.fillRect(x - 1, y + 3, width + 2, height - 3);
        g.setColor(wall);
        g.fillRect(x, y + 4, width, height - 4);
        g.setColor(roof);
        g.fillRect(x - 2, y, width + 4, 5);
        g.setColor(roof.brighter());
        g.fillRect(x - 2, y, width + 4, 1);
        g.setColor(new Color(0x536376));
        g.fillRect(x + 5, y + 8, 7, 6);
        g.fillRect(x + width - 12, y + 8, 7, 6);
        g.setColor(new Color(0xB6D2D5));
        g.fillRect(x + 6, y + 9, 2, 4);
        g.fillRect(x + width - 11, y + 9, 2, 4);
        g.setColor(new Color(0x364452));
        g.fillRect(x + width / 2 - 3, y + height - 9, 6, 9);
        g.setColor(new Color(0xF4DF9A));
        g.fillRect(x + width / 2 + 1, y + height - 5, 1, 1);
        g.setColor(new Color(0x172D40));
        int textWidth = PixelFont.width(label);
        g.fillRect(x + (width - textWidth) / 2 - 2, y + 3, textWidth + 4, 8);
        g.setColor(new Color(0xFFF0CA));
        PixelFont.draw(g, label, x + (width - textWidth) / 2, y + 9);
    }

    public static void tree(Graphics2D g, int x, int y) {
        g.setColor(new Color(0x7C6D4C));
        g.fillRect(x - 1, y, 3, 8);
        g.setColor(new Color(0x447457));
        g.fillRect(x - 6, y - 8, 13, 11);
        g.fillRect(x - 3, y - 12, 7, 5);
        g.setColor(new Color(0x6B995E));
        g.fillRect(x - 5, y - 7, 8, 3);
        g.fillRect(x - 2, y - 11, 4, 3);
    }

    public static void person(Graphics2D g, int x, int y, Color shirt, boolean step, boolean sleeping) {
        g.setColor(new Color(0x314351));
        g.fillRect(x - 4, y - 12, 8, 4);
        g.setColor(new Color(0xF1BE94));
        g.fillRect(x - 3, y - 9, 6, 4);
        g.setColor(new Color(0x263847));
        g.fillRect(x - 1, y - 8, 1, 1);
        g.fillRect(x + 2, y - 8, 1, 1);
        g.setColor(shirt);
        g.fillRect(x - 4, y - 5, 8, 5);
        g.setColor(shirt.brighter());
        g.fillRect(x - 4, y - 5, 2, 4);
        g.setColor(new Color(0xF1BE94));
        g.fillRect(x - 5, y - (step ? 5 : 3), 1, 2);
        g.fillRect(x + 4, y - (step ? 3 : 5), 1, 2);
        g.setColor(new Color(0x273C51));
        if (!sleeping) {
            g.fillRect(x - 3, y, 2, step ? 2 : 3);
            g.fillRect(x + 1, y, 2, step ? 3 : 2);
        }
    }
}
