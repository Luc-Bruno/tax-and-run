package taxandrun.model.agent;

import taxandrun.model.game.GameContext;
import taxandrun.model.state.State;
import taxandrun.model.state.StateMachine;
import taxandrun.model.world.Vector2;

public abstract class Agent<T extends Agent<T>> {
    private final String name;
    private final Vector2 home;
    private final Vector2 post;
    private Vector2 position;
    private Vector2 target;
    private double speed;
    private boolean sleeping;
    private boolean moving;
    private String feedback = "";
    private double feedbackRemaining;
    private double jumpRemaining;
    private StateMachine<T> machine;

    protected Agent(String name, Vector2 home, Vector2 post) {
        this.name = name;
        this.home = home;
        this.post = post;
        this.position = home;
    }

    protected final void initialize(T owner, State<T> initial, GameContext context) {
        machine = new StateMachine<>(owner, name, context);
        machine.changeState(initial, "simulation initialized");
        context.events().subscribe(machine::onEvent);
    }

    public void update(double dt) {
        moving = false;
        feedbackRemaining = Math.max(0, feedbackRemaining - dt);
        jumpRemaining = Math.max(0, jumpRemaining - dt);
        machine.update(dt);
    }

    public void setTarget(Vector2 target, double speed) {
        this.target = target;
        this.speed = speed;
    }

    public boolean move(double dt) {
        if (target == null) return true;
        Vector2 next = position.moveTowards(target, speed * dt);
        moving = !next.equals(position);
        position = next;
        return position.distanceTo(target) < 0.000001;
    }

    public void follow(Vector2 target, double speed, double spacing, double dt) {
        setTarget(target, speed);
        double distance = Math.max(0, position.distanceTo(target) - spacing);
        move(Math.min(dt, distance / speed));
    }

    public void clearTarget() { target = null; moving = false; }
    public void sleepAtHome() { position = home; sleeping = true; }
    public void wakeUp() { sleeping = false; }
    public void showFeedback(String text) { feedback = text; feedbackRemaining = 1.0; }
    public void jump() { jumpRemaining = 0.35; }
    public String name() { return name; }
    public Vector2 position() { return position; }
    public Vector2 home() { return home; }
    public Vector2 post() { return post; }
    public boolean atPost() { return position.distanceTo(post) < 0.000001; }
    public boolean atHome() { return position.distanceTo(home) < 0.000001; }
    public boolean sleeping() { return sleeping; }
    public boolean moving() { return moving; }
    public String feedback() { return feedbackRemaining > 0 ? feedback : ""; }
    public double feedbackRemaining() { return feedbackRemaining; }
    public double jumpRemaining() { return jumpRemaining; }
    public StateMachine<T> machine() { return machine; }
    public String stateName() { return machine.name(); }
}
