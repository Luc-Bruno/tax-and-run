package taxandrun.model.world;

import java.util.List;

public final class WorldLayout {
    public final Vector2 workerHome = new Vector2(247, 137);
    public final Vector2 bossHome = new Vector2(160, 137);
    public final Vector2 taxHome = new Vector2(73, 137);
    public final Vector2 workerPost = new Vector2(249, 73);
    public final Vector2 bossPost = new Vector2(227, 73);
    public final Vector2 bankPost = new Vector2(73, 73);
    public final List<Vector2> escapeRoute = List.of(
            new Vector2(232, 89), new Vector2(258, 108),
            new Vector2(218, 119), new Vector2(104, 119),
            new Vector2(62, 108), new Vector2(92, 89));

    public int closestWaypoint(Vector2 position) {
        int closest = 0;
        for (int i = 1; i < escapeRoute.size(); i++) {
            if (position.distanceTo(escapeRoute.get(i))
                    < position.distanceTo(escapeRoute.get(closest))) closest = i;
        }
        return closest;
    }
}
