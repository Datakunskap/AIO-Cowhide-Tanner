package script.java.tanner.data;

import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;

public enum Location {
    GE_AREA(Area.polygonal(
            new Position(3148, 3490, 0),
            new Position(3152, 3501, 0),
            new Position(3165, 3506, 0),
            new Position(3176, 3502, 0),
            new Position(3181, 3489, 0),
            new Position(3177, 3477, 0),
            new Position(3164, 3473, 0),
            new Position(3152, 3477, 0))),
    COW_AREA(Area.rectangular(3253, 3255, 3264, 3297),
                Area.rectangular(3242, 3298, 3255, 3278));

    private Area[] area;

    Location(Area... area) {
        this.area = area;
    }

    public Area[] getCowArea() {
        return area;
    }

    public boolean containsPlayer() {
        for (Area a : area) {
            if (a.contains(Players.getLocal()))
                return true;
        }
        return false;
    }
}
