package taxandrun.model.agent;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import taxandrun.model.agent.worker.WorkerSleepingState;
import taxandrun.model.event.GameEvent;
import taxandrun.model.event.GameEventType;
import taxandrun.model.game.GameContext;

public final class Worker extends Agent<Worker> {
    public enum ChaseReason { BOSS, TAX_COLLECTOR }
    private int balance;
    private int dailyProduction;
    private int previousDayProduction;
    private boolean workEnabled;
    private final EnumSet<ChaseReason> pursuers = EnumSet.noneOf(ChaseReason.class);

    public Worker(GameContext context) {
        super("WORKER", context.layout().workerHome, context.layout().workerPost);
        initialize(this, new WorkerSleepingState(), context);
    }

    public void prepareDay() {
        previousDayProduction = dailyProduction;
        dailyProduction = 0;
        pursuers.clear();
    }

    public boolean canWork(GameContext context) {
        return workEnabled && context.phase() == taxandrun.model.game.GamePhase.DAY;
    }

    public void performWork(GameContext context) {
        if (!canWork(context)) return;
        balance++;
        dailyProduction++;
        jump();
        showFeedback("+1");
        context.events().publish(new GameEvent(GameEventType.WORK_PERFORMED, name(),
                "balance=" + balance + " production=" + dailyProduction, 1));
    }

    public void pay(int amount) {
        if (amount < 0 || amount > balance) throw new IllegalArgumentException("Invalid payment");
        balance -= amount;
        showFeedback("-" + amount);
    }

    public void setWorkEnabled(boolean enabled) { workEnabled = enabled; }
    public void addPursuer(ChaseReason reason) { pursuers.add(reason); }
    public void clearPursuers() { pursuers.clear(); }
    public Set<ChaseReason> pursuers() { return Collections.unmodifiableSet(pursuers); }
    public int balance() { return balance; }
    public int dailyProduction() { return dailyProduction; }
    public int previousDayProduction() { return previousDayProduction; }
}
