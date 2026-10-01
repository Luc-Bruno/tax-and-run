package taxandrun.model.world;

public record Vector2(double x, double y) {
    public double distanceTo(Vector2 other) {
        return Math.hypot(other.x - x, other.y - y);
    }

    public Vector2 moveTowards(Vector2 target, double distance) {
        double remaining = distanceTo(target);
        if (remaining <= distance || remaining < 0.000001) return target;
        return new Vector2(x + (target.x - x) * distance / remaining,
                y + (target.y - y) * distance / remaining);
    }
}
