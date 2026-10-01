package taxandrun.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import taxandrun.model.game.GameSnapshot.AgentSnapshot;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameSnapshot;
import taxandrun.model.game.GamePhase;

public final class GameRenderer {
    private static final Rectangle WORK_BUTTON = new Rectangle(239, 151, 72, 21);
    private static final Color INK = new Color(0x172D40);
    private static final Color PAPER = new Color(0xF7EDD2);
    private static final Color MUTED = new Color(0xA7BBBD);
    private static final Color GOLD = new Color(0xF3CB70);
    private static final Color WORKER = new Color(0x4689C8);
    private static final Color BOSS = new Color(0xD88B4C);
    private static final Color TAX = new Color(0xA45F77);

    public static boolean workButtonContains(Point point) { return WORK_BUTTON.contains(point); }

    public void render(Graphics2D g, GameSnapshot game) {
        GameSnapshot context = game;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        drawWorld(g, game);
        drawHud(g, context);
        drawStatePanel(g, game);
        if (game.paused()) {
            g.setColor(new Color(0, 0, 0, 110));
            g.fillRect(0, 38, 320, 106);
            g.setColor(INK);
            g.fillRect(112, 79, 96, 24);

            g.setColor(GOLD);
            centered(g, "PAUSED", 160, 89);

            g.setColor(PAPER);
            centered(g, "P TO RESUME", 160, 98);
        }
    }

    private void drawWorld(Graphics2D g, GameSnapshot game) {
        GameSnapshot context = game;
        g.setColor(new Color(0x96B77A));
        g.fillRect(0, 38, 320, 106);
        g.setColor(new Color(0x86A76D));
        for (int x = 8; x < 320; x += 19) {
            for (int y = 45; y < 142; y += 17) {
                g.fillRect(x, y, 2, 1);
                g.fillRect(x + 4, y + 2, 1, 1);
            }
        }
        g.setColor(new Color(0xC9BD95));
        g.fillRect(34, 73, 252, 12);
        g.fillRect(45, 85, 230, 39);
        g.fillRect(66, 71, 14, 67);
        g.fillRect(153, 120, 14, 18);
        g.fillRect(240, 71, 14, 67);
        g.setColor(new Color(0xB4AA88));
        g.drawRect(45, 85, 230, 39);
        g.setColor(new Color(0x92B378));
        g.fillRect(91, 98, 137, 12);
        g.setColor(new Color(0x80A06B));
        g.fillRect(92, 110, 136, 2);
        g.setColor(new Color(0xB6CB91));
        for (int x = 96; x < 225; x += 15) g.fillRect(x, 104, 3, 1);
        PixelArtFactory.tree(g, 16, 65);
        PixelArtFactory.tree(g, 300, 64);
        PixelArtFactory.tree(g, 20, 115);
        PixelArtFactory.tree(g, 299, 118);
        PixelArtFactory.building(g, 49, 43, 48, 26,
                new Color(0xDBD8BF), new Color(0x708A91), "BANK");
        PixelArtFactory.building(g, 214, 43, 51, 26,
                new Color(0xF0D7A3), new Color(0xB66B54), "SHOP");
        PixelArtFactory.building(g, 58, 124, 30, 18,
                new Color(0xD6CDBB), TAX, "T");
        PixelArtFactory.building(g, 145, 124, 30, 18,
                new Color(0xE2CFB0), BOSS, "B");
        PixelArtFactory.building(g, 232, 124, 30, 18,
                new Color(0xD5D9C8), WORKER, "W");

        List<AgentSnapshot> ordered = new ArrayList<>(context.agents());
        ordered.sort(Comparator.comparingDouble(a -> a.position().y()));
        for (AgentSnapshot agent : ordered) drawAgent(g, agent, context);

        if (context.phase() == GamePhase.NIGHT) {
            g.setComposite(AlphaComposite.SrcOver.derive(0.43f));
            g.setColor(new Color(0x182643));
            g.fillRect(0, 38, 320, 106);
            g.setComposite(AlphaComposite.SrcOver);
            g.setColor(new Color(0xF7EDD2));
            g.fillRect(291, 43, 7, 7);
            g.setColor(new Color(0x536C66));
            g.fillRect(294, 42, 5, 5);
            if (!context.countdownRunning()) drawStageBanner(g, "GOING HOME");
        } else if (context.phase() == GamePhase.MORNING) {
            drawStageBanner(g, game.awaitingTaxCollection() ? "TAX COLLECTION" : "GOING TO WORK");
        }
    }

    private void drawStageBanner(Graphics2D g, String text) {
        g.setColor(INK);
        g.fillRect(117, 43, 87, 10);
        g.setColor(PAPER);
        centered(g, text, 160, 50);
    }

    private void drawAgent(Graphics2D g, AgentSnapshot agent, GameSnapshot context) {
        int x = (int) Math.round(agent.position().x());
        int floor = (int) Math.round(agent.position().y());
        int jump = (int) Math.round(Math.sin(agent.jumpRemaining() / 0.35 * Math.PI) * 4);
        int idleBob = !agent.moving() && !agent.sleeping() && agent.jumpRemaining() == 0
                ? (int) (context.elapsed() * 2) % 2 : 0;
        int y = floor - jump - idleBob;
        Color color = agent == context.worker() ? WORKER : agent == context.boss() ? BOSS : TAX;
        g.setColor(new Color(0x687C62));
        g.fillRect(x - 5, floor + 2, 10, 2);
        boolean step = agent.moving() && (int) (context.elapsed() * 9) % 2 == 0;
        PixelArtFactory.person(g, x, y, color, step, agent.sleeping());

        String symbol = agent.feedback();
        if (agent.sleeping()) symbol = "Z".repeat(1 + (int) context.elapsed() % 3);
        else if (symbol.isEmpty() && agent.stateName().equals("ANGRY")) symbol = "!";
        else if (symbol.isEmpty() && agent.stateName().equals("IDLE")) symbol = "...";
        if (!symbol.isEmpty()) {
            int width = PixelFont.width(symbol);
            g.setColor(INK);
            g.fillRect(x - width / 2 - 2, y - 23, width + 4, 9);
            g.setColor(GOLD);
            centered(g, symbol, x, y - 16);
        }
    }

    private void drawHud(Graphics2D g, GameSnapshot context) {
        g.setColor(INK);
        g.fillRect(0, 0, 320, 38);

        g.setColor(GOLD);
        PixelFont.draw(g, "TAX & RUN", 8, 11);
        g.setColor(PAPER);
        PixelFont.draw(g, "DAY " + context.day(), 149, 11);

        g.setColor(MUTED);
        PixelFont.draw(g, context.phase().name(), 208, 11);
        g.setColor(PAPER);
        String timer = String.format(java.util.Locale.ROOT, "%04.1fs", context.remainingSeconds());
        PixelFont.draw(g, timer, 282, 11);
        PixelFont.draw(g, "BALANCE $" + context.balance(), 8, 22);
        PixelFont.draw(g, "TODAY " + context.dailyProduction(), 101, 22);
        PixelFont.draw(g, "PREV " + context.previousDayProduction(), 174, 22);
        g.setColor(context.warningCountToday() > 0 ? GOLD : MUTED);
        PixelFont.draw(g, "WARN " + context.warningCountToday() + "/2", 259, 22);
        g.setColor(MUTED);
        PixelFont.draw(g, "TAX: " + context.taxStatus(), 8, 31);
        PixelFont.draw(g, "WORK. PAY. OR RUN.", 208, 31);
        g.setColor(new Color(0x385061));
        g.fillRect(0, 35, 320, 3);
        double ratio = context.remainingSeconds() / (context.phase() == GamePhase.NIGHT
                ? GameConfig.NIGHT_SECONDS : GameConfig.DAY_SECONDS);
        g.setColor(context.phase() == GamePhase.NIGHT ? new Color(0x8494B8) : GOLD);
        g.fillRect(0, 35, (int) Math.round(ratio * 320), 3);
    }

    private void drawStatePanel(Graphics2D g, GameSnapshot game) {
        GameSnapshot context = game;
        g.setColor(INK);
        g.fillRect(0, 142, 320, 38);
        g.setColor(new Color(0x385061));
        g.drawLine(8, 143, 311, 143);
        drawState(g, "WORKER", context.worker().stateName(), WORKER, 151);
        drawState(g, "BOSS", context.boss().stateName(), BOSS, 160);
        drawState(g, "TAX COLLECTOR", context.taxCollector().stateName(), TAX, 169);

        g.setColor(game.canWork() ? new Color(0xAB793D) : new Color(0x304758));
        g.fillRect(WORK_BUTTON.x, WORK_BUTTON.y + 2, WORK_BUTTON.width, WORK_BUTTON.height);
        g.setColor(game.canWork() ? GOLD : new Color(0x567080));
        g.fillRect(WORK_BUTTON.x, WORK_BUTTON.y, WORK_BUTTON.width, WORK_BUTTON.height - 1);

        g.setColor(game.canWork() ? INK : new Color(0xBDCDD0));
        centered(g, "WORK!", 275, 160);

        centered(g, game.canWork() ? "[SPACE]" : "UNAVAILABLE", 275, 168);
        g.setColor(MUTED);
        PixelFont.draw(g, "P PAUSE  R RESET  ESC EXIT", 8, 179);
    }

    private void drawState(Graphics2D g, String name, String state, Color color, int y) {
        g.setColor(color);
        g.fillRect(8, y - 5, 3, 4);
        g.setColor(MUTED);
        PixelFont.draw(g, name, 15, y);
        g.setColor(PAPER);
        PixelFont.draw(g, state, 103, y);
    }

    private void centered(Graphics2D g, String text, int x, int y) {
        PixelFont.draw(g, text, x - PixelFont.width(text) / 2, y);
    }
}
