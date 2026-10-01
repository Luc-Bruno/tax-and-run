package taxandrun;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import taxandrun.model.agent.Agent;
import taxandrun.model.game.GameConfig;
import taxandrun.controller.GameController;
import taxandrun.model.game.GamePhase;

public final class SimulationTest {
    private static int checks;

    public static void main(String[] args) {
        normalWork();
        warnings();
        payment(10, 5);
        payment(25, 15);
        insufficientFunds();
        evasionAndDoubleChase();
        nightAndReset();
        boundaries();
        pauseAndResetPreparation();
        controlsAndLogs();
        System.out.println("PASS: " + checks + " assertions; all nine specification scenarios covered.");
    }

    private static GameController game() { return new GameController(line -> {}); }

    private static void check(boolean condition, String message) {
        checks++;
        if (!condition) throw new AssertionError(message);
    }

    private static void until(GameController game, BooleanSupplier condition, double timeout) {
        for (int i = 0; i < (int) Math.ceil(timeout / GameConfig.STEP_SECONDS); i++) {
            if (condition.getAsBoolean()) return;
            game.update(GameConfig.STEP_SECONDS);
        }
        check(condition.getAsBoolean(), "Condition not reached within " + timeout + " seconds");
    }

    private static void day(GameController game, int day) {
        until(game, () -> game.context().day() == day && game.context().phase() == GamePhase.DAY, 55);
    }

    private static void work(GameController game, int count) {
        for (int i = 0; i < count; i++) game.requestWork();
        game.update(0);
    }

    private static void normalWork() {
        GameController game = game();
        work(game, 1);
        check(game.context().worker().balance() == 0, "No work during MORNING");
        day(game, 1);
        check(game.context().agents().stream().allMatch(Agent::atPost), "All agents arrived before DAY");
        check(game.context().taxCollector().stateName().equals("WAITING"), "Day 1 has no tax");
        check(game.context().remainingSeconds() == 40, "No-tax day starts with all 40 seconds");
        check(game.context().boss().inactivityTimer() == 0, "Morning travel does not count as inactivity");
        check(game.canWork(), "No-tax day immediately enables work once positioned");
        for (int i = 0; i < 9; i++) {
            game.update(4);
            work(game, 1);
        }
        check(game.context().worker().balance() == 9, "One coin per valid click");
        check(game.context().worker().dailyProduction() == 9, "Daily production matches work");
        check(game.context().boss().warningCountToday() == 0, "Regular work prevents warnings");
        check(game.context().boss().stateName().equals("WATCHING"), "Boss keeps watching");
        check(game.context().worker().stateName().equals("WORKING"), "Worker remains working");
    }

    private static void warnings() {
        GameController game = game();
        day(game, 1);
        game.update(4.99);
        check(game.context().boss().warningCountToday() == 0, "No warning before 5s");
        game.update(0.01);
        check(game.context().boss().stateName().equals("ANGRY"), "First warning at 5s");
        check(game.context().worker().stateName().equals("IDLE"), "Worker idles after first warning");
        check(game.canWork(), "Work is allowed while idle");
        work(game, 1);
        check(game.context().worker().stateName().equals("WORKING"), "First idle click resumes and works");
        check(game.context().worker().balance() == 1, "First idle click grants a coin");
        check(game.context().boss().warningCountToday() == 1, "Work does not erase warnings");
        game.update(0.8);
        check(game.context().boss().stateName().equals("WATCHING"), "First anger ends after 0.8s");
        game.update(5);
        check(game.context().boss().warningCountToday() == 2, "Second inactivity warning");
        game.update(0.8);
        check(game.context().boss().stateName().equals("CHASING"), "Boss chases after second anger");
        check(game.context().worker().stateName().equals("FLEEING"), "Worker receives chase event");
        work(game, 1);
        check(game.context().worker().balance() == 1, "No production during fleeing");
    }

    private static GameController withPreviousProduction(int production) {
        GameController game = game();
        day(game, 1);
        work(game, production);
        until(game, () -> game.context().day() == 2
                && (game.awaitingTaxCollection() || game.context().phase() == GamePhase.DAY), 60);
        check(game.context().worker().previousDayProduction() == production, "Previous production rolls over");
        check(game.context().worker().dailyProduction() == 0, "Daily production resets");
        check(game.context().boss().warningCountToday() == 0, "Warnings reset");
        check(game.context().worker().balance() == production, "Balance persists between days");
        return game;
    }

    private static void payment(int production, int expectedTax) {
        GameController game = withPreviousProduction(production);
        check(game.context().taxCollector().taxDue() == expectedTax, "Correct tax bracket");
        check(game.context().taxCollector().stateName().equals("GOING_TO_COLLECT"), "Collection starts after all agents reach their posts");
        check(game.context().agents().stream().allMatch(Agent::atPost), "Collection starts from the bank after everyone arrives");
        check(game.context().phase() == GamePhase.MORNING, "Collection belongs to morning preparation");
        check(!game.canWork(), "No work before collection");
        check(game.context().worker().balance() == production, "No debit before collector arrives");
        work(game, 2);
        game.update(2);
        check(game.context().worker().balance() == production, "Physical travel precedes payment");
        check(game.context().worker().dailyProduction() == 0, "Blocked clicks cannot produce coins");
        check(game.context().remainingSeconds() == 40, "Day timer is frozen during collection travel");
        check(game.context().boss().inactivityTimer() == 0, "Boss timer is frozen during collection travel");
        until(game, () -> game.context().taxCollector().stateName().equals("COLLECTING"), 4);
        check(!game.canWork() && game.context().remainingSeconds() == 40, "Arrival alone does not enable work or clocks");
        check(game.context().taxCollector().position().distanceTo(game.context().worker().position())
                <= GameConfig.COLLECTION_DISTANCE, "Collection happens at worker");
        game.update(GameConfig.STEP_SECONDS);
        check(game.context().worker().balance() == production - expectedTax, "Payment amount deducted once");
        check(game.context().taxCollector().stateName().equals("RETURNING_TO_BANK"), "Returns after payment");
        check(game.context().phase() == GamePhase.DAY && game.context().remainingSeconds() == 40, "Payment starts a full 40-second day");
        check(game.context().boss().stateName().equals("WATCHING") && game.context().boss().inactivityTimer() == 0,
                "Boss starts monitoring only after payment");
        check(game.canWork(), "Work enabled after payment without waiting for return to bank");
        game.update(4.99);
        check(game.context().boss().warningCountToday() == 0, "Morning collection cannot shorten the first warning interval");
        game.update(0.01);
        check(game.context().boss().warningCountToday() == 1, "First warning is exactly 5s after payment");
        work(game, 1);
        until(game, () -> game.context().taxCollector().stateName().equals("WAITING"), 6);
        check(game.context().worker().balance() == production - expectedTax + 1, "No duplicate tax on return to waiting");
    }

    private static void insufficientFunds() {
        GameController game = withPreviousProduction(3);
        work(game, 2);
        until(game, () -> game.context().taxCollector().stateName().equals("CHASING"), 6);
        check(game.context().worker().stateName().equals("FLEEING"), "Insufficient funds trigger fleeing");
        check(game.context().worker().balance() == 3, "Failed payment never makes balance negative");
        check(game.context().worker().dailyProduction() == 0, "Cannot earn extra coins before collection");
        check(game.context().phase() == GamePhase.DAY && game.context().remainingSeconds() == 40, "Insufficient funds still release the day clock");
        check(game.context().boss().inactivityTimer() == 0, "No pre-warning time is carried from collection");
        check(!game.canWork(), "Fleeing keeps work disabled after clocks start");
    }

    private static void evasionAndDoubleChase() {
        GameController game = withPreviousProduction(41);
        check(game.context().worker().atPost() && game.context().worker().pursuers().isEmpty(),
                "High production does not flee before arrival");
        check(!game.canWork(), "High-production worker waits for collection before day");
        until(game, () -> game.context().taxCollector().stateName().equals("CHASING"), 6);
        check(game.context().worker().balance() == 41, "High production evades without payment");
        check(game.context().boss().stateName().equals("WATCHING"), "Tax chase does not directly start boss chase");
        check(game.context().remainingSeconds() == 40, "Evasion starts a full day");
        game.update(4.99);
        check(game.context().boss().warningCountToday() == 0, "Boss does not warn early after evasion");
        game.update(0.01);
        check(game.context().boss().warningCountToday() == 1, "First warning after evasion takes full 5 seconds");
        check(game.context().worker().stateName().equals("FLEEING"), "First boss warning cannot cancel tax fleeing");
        until(game, () -> game.context().boss().stateName().equals("CHASING"), 8);
        check(game.context().taxCollector().stateName().equals("CHASING"), "Double chase supported");
        check(game.context().worker().pursuers().size() == 2, "Both chase reasons recorded");
        check(game.context().boss().warningCountToday() == 2, "Boss follows its own two-warning rule");
        game.update(8);
        check(game.context().worker().stateName().equals("FLEEING"), "No captured state");
    }

    private static void nightAndReset() {
        GameController game = withPreviousProduction(41);
        until(game, () -> game.context().boss().stateName().equals("CHASING"), 20);
        game.update(game.context().remainingSeconds());
        check(game.context().phase() == GamePhase.NIGHT, "DAY ends after exactly 40s");
        check(game.context().agents().stream().allMatch(a -> a.stateName().equals("GOING_HOME")), "Night interrupts all pursuits");
        check(!game.canWork(), "Night disables work");
        check(game.context().worker().pursuers().isEmpty(), "Night clears pursuers");
        check(game.context().remainingSeconds() == 5 && !game.context().countdownRunning(),
                "Night starts with frozen clock while agents return");
        double returnStarted = game.context().elapsed();
        boolean frozenDuringReturn = true;
        boolean firstArrivalBeforeLast = false;
        for (int i = 0; i < 600 && !game.context().countdownRunning(); i++) {
            long sleeping = game.context().agents().stream().filter(Agent::sleeping).count();
            if (sleeping > 0 && sleeping < 3) firstArrivalBeforeLast = true;
            game.update(GameConfig.STEP_SECONDS);
            frozenDuringReturn &= game.context().remainingSeconds() == 5;
        }
        check(frozenDuringReturn, "Return-home animation consumes none of the sleeping time");
        check(firstArrivalBeforeLast, "First arrival cannot start the clock before other agents arrive");
        check(game.context().elapsed() > returnStarted, "Return-home movement actually ran");
        check(game.context().countdownRunning(), "All arrivals start the night clock");
        check(game.context().agents().stream().allMatch(Agent::sleeping), "All sleep before next morning");
        check(game.context().agents().stream().allMatch(Agent::atHome), "All at home");
        game.update(4.99);
        check(game.context().phase() == GamePhase.NIGHT, "All agents sleep for a full 5 seconds");
        game.update(0.01);
        check(game.context().phase() == GamePhase.MORNING, "Morning after 5s");
        day(game, 3);
        check(game.context().worker().previousDayProduction() == 0, "Prior fleeing day produced nothing");
        check(game.context().taxCollector().taxDue() == 0, "No tax debt carries over");
        check(game.context().taxCollector().stateName().equals("WAITING"), "No collection on zero production");
        check(game.context().worker().balance() == 41, "Savings preserved across evasion cycle");
    }

    private static void boundaries() {
        for (int production : new int[] {0, 1, 20, 21, 40, 41}) {
            GameController game = withPreviousProduction(production);
            int expected = production == 0 || production > 40 ? 0 : production <= 20 ? 5 : 15;
            check(game.context().taxCollector().taxDue() == expected, "Bracket boundary " + production);
        }
        GameController game = withPreviousProduction(5);
        until(game, () -> game.context().taxCollector().stateName().equals("RETURNING_TO_BANK"), 6);
        check(game.context().worker().balance() == 0, "Exact balance pays tax without going negative");
    }

    private static void pauseAndResetPreparation() {
        GameController game = withPreviousProduction(10);
        game.update(1);
        var position = game.context().taxCollector().position();
        game.togglePause();
        game.update(30);
        check(game.context().taxCollector().position().equals(position), "Pause freezes collection animation");
        check(game.awaitingTaxCollection() && game.context().remainingSeconds() == 40,
                "Pause cannot skip morning preparation");
        game.togglePause();
        game.reset();
        check(!game.awaitingTaxCollection(), "Reset clears pending collection");
        day(game, 1);
        check(game.canWork() && game.context().remainingSeconds() == 40, "Reset starts first day without stale collection");
        game.update(40);
        position = game.context().worker().position();
        game.togglePause();
        game.update(30);
        check(game.context().worker().position().equals(position), "Pause freezes return-home animation");
        check(game.context().remainingSeconds() == 5 && !game.context().countdownRunning(),
                "Pause does not start night timer before arrivals");
        game.togglePause();
        until(game, () -> game.context().countdownRunning(), 6);
        game.update(1);
        game.togglePause();
        double remaining = game.context().remainingSeconds();
        game.update(30);
        check(game.context().remainingSeconds() == remaining, "Pause also freezes sleeping countdown");
        game.reset();
        check(!game.context().countdownRunning(), "Reset clears running night countdown");
    }

    private static void controlsAndLogs() {
        List<String> logs = new ArrayList<>();
        GameController game = new GameController(logs::add);
        day(game, 1);
        work(game, 2);
        check(game.context().worker().balance() == 2, "No mandatory click cooldown");
        double remaining = game.context().remainingSeconds();
        game.togglePause();
        work(game, 5);
        game.update(100);
        check(game.context().remainingSeconds() == remaining, "Pause freezes simulation");
        check(game.context().worker().balance() == 2, "Pause discards work");
        game.togglePause();
        game.update(5.8);
        check(logs.stream().anyMatch(s -> s.contains("ENTER ANGRY")), "Entry log");
        check(logs.stream().anyMatch(s -> s.contains("EXIT WATCHING")), "Exit log");
        check(logs.stream().anyMatch(s -> s.contains("reason=5 seconds without work")), "Reason log");
        check(logs.stream().anyMatch(s -> s.contains("WORK_PERFORMED received")), "Communication log");
        game.reset();
        check(game.context().day() == 1 && game.context().worker().balance() == 0, "Reset restores initial data");
        check(game.context().phase() == GamePhase.MORNING && !game.paused(), "Reset restores phase and pause");
        day(game, 1);
        work(game, 1);
        check(game.context().worker().balance() == 1, "Reset does not duplicate event subscriptions");
    }
}
