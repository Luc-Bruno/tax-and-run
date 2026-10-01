package taxandrun.model.agent;

import taxandrun.model.agent.tax.TaxSleepingState;
import taxandrun.model.game.GameContext;

public final class TaxCollector extends Agent<TaxCollector> {
    private int taxDue;
    private boolean attemptedToday;
    private String taxStatus = "NO TAX";

    public TaxCollector(GameContext context) {
        super("TAX", context.layout().taxHome, context.layout().bankPost);
        initialize(this, new TaxSleepingState(), context);
    }

    public void prepareDay() { taxDue = 0; attemptedToday = false; taxStatus = "NO TAX"; }
    public boolean assess(int previousProduction) {
        if (attemptedToday) return false;
        attemptedToday = true;
        taxDue = previousProduction == 0 || previousProduction > 40 ? 0
                : previousProduction <= 20 ? 5 : 15;
        boolean collect = previousProduction > 0;
        taxStatus = !collect ? "NO TAX" : previousProduction > 40
                ? "VISIT (>40)" : "DUE $" + taxDue;
        return collect;
    }
    public void setTaxStatus(String status) { taxStatus = status; }
    public int taxDue() { return taxDue; }
    public boolean attemptedToday() { return attemptedToday; }
    public String taxStatus() { return taxStatus; }
}
