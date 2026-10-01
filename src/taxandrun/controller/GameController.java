package taxandrun.controller;

import java.util.function.Consumer;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;
import taxandrun.model.game.GameLog;
import taxandrun.model.game.GamePhase;
import taxandrun.model.game.GameSnapshot;
import taxandrun.model.agent.Agent;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;

public final class GameController {
    private final GameLog log;
    private GameContext context;
    private boolean paused;
    private int pendingWork;
    private boolean morningCollectionStarted;
    private boolean morningCollectionResolved;

    public GameController() { this(System.out::println); }
    public GameController(Consumer<String> logOutput) {
        log = new GameLog(logOutput);
        reset();
    }

    public void reset() {
        context = new GameContext(log);
        context.events().subscribe(this::onEvent);
        paused = false;
        pendingWork = 0;
        morningCollectionStarted = false;
        morningCollectionResolved = false;
        log.write(1, "GAME", "simulation started | phase=MORNING");
    }

    public void requestWork() { if (canWork()) pendingWork++; }

    public void togglePause() {
        paused = !paused;
        pendingWork = 0;
        log.write(context.day(), "GAME", paused ? "paused" : "resumed");
    }

    public void update(double deltaTime) {
        if (!Double.isFinite(deltaTime) || deltaTime < 0) {
            throw new IllegalArgumentException("deltaTime must be finite and nonnegative");
        }
        if (paused) return;
        while (pendingWork > 0) {
            pendingWork--;
            context.events().publish(new GameEvent(GameEventType.WORK_REQUESTED, "PLAYER", "WORK!"));
            context.events().dispatchPending();
        }
        // Passos pequenos mantêm movimento e transições consistentes mesmo em testes sem janela.
        double remaining = deltaTime;
        while (remaining > 1e-9) {
            double step = Math.min(GameConfig.STEP_SECONDS, remaining);
            if (context.countdownRunning() && context.remainingSeconds() > 1e-9) {
                step = Math.min(step, context.remainingSeconds());
            }
            advanceStep(step);
            remaining -= step;
        }
    }

    private void advanceStep(double dt) {
        context.advanceTime(dt);
        context.events().dispatchPending();
        for (Agent<?> agent : context.agents()) agent.update(dt);
        context.events().dispatchPending();

        if (context.phase() == GamePhase.MORNING) {
            if (!morningCollectionStarted && context.agents().stream().allMatch(Agent::atPost)) {
                prepareMorningCollection();
            }
            if (morningCollectionResolved) beginDay();
        } else if (context.phase() == GamePhase.DAY
                && context.phaseElapsed() + 1e-9 >= GameConfig.DAY_SECONDS) {
            context.setPhase(GamePhase.NIGHT);
            context.events().publish(new GameEvent(GameEventType.NIGHT_STARTED, "GAME", "day timer expired"));
            context.events().dispatchPending();
        } else if (context.phase() == GamePhase.NIGHT) {
            if (!context.countdownRunning()
                    && context.agents().stream().allMatch(a -> a.atHome() && a.sleeping())) {
                context.startNightCountdown();
            }
            if (context.phaseElapsed() + 1e-9 >= GameConfig.NIGHT_SECONDS) {
                context.nextDay();
                morningCollectionStarted = false;
                morningCollectionResolved = false;
                context.setPhase(GamePhase.MORNING);
            }
        }
    }

    private void prepareMorningCollection() {
        morningCollectionStarted = true;
        context.worker().prepareDay();
        context.boss().prepareDay();
        context.taxCollector().prepareDay();
        context.events().publish(new GameEvent(GameEventType.POSTS_REACHED, "GAME",
                "all agents positioned; resolve tax before starting day"));
        context.events().dispatchPending();
    }

    private void onEvent(GameEvent event) {
        if (event.type() == GameEventType.TAX_COLLECTION_RESOLVED
                && context.phase() == GamePhase.MORNING && morningCollectionStarted) {
            morningCollectionResolved = true;
        }
    }

    private void beginDay() {
        context.setPhase(GamePhase.DAY);
        context.events().publish(new GameEvent(GameEventType.DAY_STARTED, "GAME",
                "tax resolved; day and boss timers start now"));
        context.events().dispatchPending();
    }

    public GameContext context() { return context; }
    public GameSnapshot snapshot() {
        return GameSnapshot.capture(context, paused, canWork(), awaitingTaxCollection());
    }
    public boolean paused() { return paused; }
    public boolean awaitingTaxCollection() {
        return context.phase() == GamePhase.MORNING && morningCollectionStarted;
    }
    public boolean canWork() { return !paused && context.worker().canWork(context); }
}
