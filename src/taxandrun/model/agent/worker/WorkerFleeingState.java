package taxandrun.model.agent.worker;

import taxandrun.model.agent.Worker;
import taxandrun.model.game.GameConfig;
import taxandrun.model.game.GameContext;

public final class WorkerFleeingState extends WorkerState {
    private int waypoint;
    @Override public String getName() { return "FLEEING"; }
    @Override public void enter(Worker owner, GameContext context) {
        waypoint = context.layout().closestWaypoint(owner.position());
        owner.setTarget(context.layout().escapeRoute.get(waypoint), GameConfig.FLEE_SPEED);
        owner.showFeedback("!");
    }
    @Override public void execute(Worker owner, GameContext context, double dt) {
        if (owner.move(dt)) {
            waypoint = (waypoint + 1) % context.layout().escapeRoute.size();
            owner.setTarget(context.layout().escapeRoute.get(waypoint), GameConfig.FLEE_SPEED);
        }
    }
}
