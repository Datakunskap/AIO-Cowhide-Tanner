package script.java.tanner.data;

public enum Hides {
    COWHIDE(1739),
    GREEN_DRAGONHIDE(1753),
    BLUE_DRAGONHIDE(1751),
    RED_DRAGONHIDE(1749),
    BLACK_DRAGONHIDE(1747);

    private int hideId;

    Hides(int hideId){
        this.hideId = hideId;
    }

    public int getHideId() {
        return hideId;
    }
}
