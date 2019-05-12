package lamerton.troy.tanner.data;

import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;

public enum Location {
    GE_AREA(Area.polygonal(
            new Position[] {
                    new Position(3161, 3498, 0),
                    new Position(3168, 3498, 0),
                    new Position(3173, 3493, 0),
                    new Position(3173, 3486, 0),
                    new Position(3167, 3480, 0),
                    new Position(3161, 3481, 0),
                    new Position(3156, 3486, 0),
                    new Position(3156, 3493, 0)
            }));

    private Area begArea;

    Location(Area begArea) {
        this.begArea = begArea;
    }

    public Area getGEArea() {
        return begArea;
    }
}
