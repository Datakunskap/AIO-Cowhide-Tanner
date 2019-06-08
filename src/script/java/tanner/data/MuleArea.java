package script.java.tanner.data;

import org.rspeer.runetek.api.movement.position.Area;

public enum MuleArea {
    AL_KHARID(Area.rectangular(3264, 3199, 3265, 3198)),
    GE_NE(Area.rectangular(3180, 3513, 3181, 3512)),
    GE_SE(Area.rectangular(3184, 3471, 3183, 3472)),
    GE_SW(Area.rectangular(3140, 3474, 3141, 3473)),
    GE_NW(Area.rectangular(3148, 3515, 3149, 3514))
    ;

    private Area muleArea;

    MuleArea(Area  muleArea) {
        this.muleArea = muleArea;
    }

    public Area getMuleArea(){
        return muleArea;
    }
}

