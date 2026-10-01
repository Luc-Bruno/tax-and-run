package taxandrun.model.game;

import java.util.List;
import taxandrun.model.agent.Agent;
import taxandrun.model.agent.Boss;
import taxandrun.model.agent.TaxCollector;
import taxandrun.model.agent.Worker;
import taxandrun.model.event.GameEventBus;
import taxandrun.model.world.WorldLayout;

public final class GameContext {
    private final GameLog log;
    private final WorldLayout layout = new WorldLayout();
    private final GameEventBus events;
    private final Worker worker;
    private final Boss boss;
    private final TaxCollector taxCollector;
    private GamePhase phase = GamePhase.MORNING;
    private int day = 1;
    private double phaseElapsed;
    private double elapsed;
    private boolean nightCountdownStarted;

    public GameContext(GameLog log) {
        this.log = log;
        events = new GameEventBus(log, this::day);
        worker = new Worker(this);
        boss = new Boss(this);
        taxCollector = new TaxCollector(this);
    }

    public void advanceTime(double dt) {
        elapsed += dt;
        if (countdownRunning()) phaseElapsed += dt;
    }
    public void setPhase(GamePhase phase) {
        this.phase = phase;
        phaseElapsed = 0;
        nightCountdownStarted = false;
        log.write(day, "GAME", "phase=" + phase);
    }
    public void startNightCountdown() {
        nightCountdownStarted = true;
        log.write(day, "GAME", "night countdown started | all agents home and sleeping");
    }
    public void nextDay() { day++; }
    public GameLog log() { return log; }
    public WorldLayout layout() { return layout; }
    public GameEventBus events() { return events; }
    public Worker worker() { return worker; }
    public Boss boss() { return boss; }
    public TaxCollector taxCollector() { return taxCollector; }
    public List<Agent<?>> agents() { return List.of(worker, boss, taxCollector); }
    public GamePhase phase() { return phase; }
    public int day() { return day; }
    public double phaseElapsed() { return phaseElapsed; }
    public double elapsed() { return elapsed; }
    public boolean countdownRunning() {
        return phase == GamePhase.DAY || (phase == GamePhase.NIGHT && nightCountdownStarted);
    }
    public double remainingSeconds() {
        double duration = phase == GamePhase.NIGHT
                ? GameConfig.NIGHT_SECONDS : GameConfig.DAY_SECONDS;
        return Math.max(0, duration - phaseElapsed);
    }
}
