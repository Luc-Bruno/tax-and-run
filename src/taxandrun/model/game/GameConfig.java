package taxandrun.model.game;

public final class GameConfig {
    private GameConfig() {}
    public static final double DAY_SECONDS = 40;
    public static final double NIGHT_SECONDS = 5;
    public static final double INACTIVITY_SECONDS = 5;
    public static final double ANGER_SECONDS = 0.8;
    public static final double WALK_SPEED = 40;
    public static final double FLEE_SPEED = 55;
    public static final double CHASE_SPEED = 50;
    public static final double COLLECTION_DISTANCE = 6;
    public static final double STEP_SECONDS = 1.0 / 60.0;
    public static final int VIRTUAL_WIDTH = 320;
    public static final int VIRTUAL_HEIGHT = 180;
}
