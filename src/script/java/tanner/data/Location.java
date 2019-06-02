package script.java.tanner.data;

import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

public enum Location {
    GE_AREA(Area.polygonal(
            new Position[] {
                    new Position(3148, 3490, 0),
                    new Position(3152, 3501, 0),
                    new Position(3165, 3506, 0),
                    new Position(3176, 3502, 0),
                    new Position(3181, 3489, 0),
                    new Position(3177, 3477, 0),
                    new Position(3164, 3473, 0),
                    new Position(3152, 3477, 0)
            }));

    private Area begArea;

    Location(Area begArea) {
        this.begArea = begArea;
    }

    public Area getGEArea() {
        return begArea;
    }
}
