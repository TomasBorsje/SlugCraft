package io.github.tomasborsje.slugcraft.quickfire;

import java.util.ArrayList;
import java.util.List;

public class StartPoints {
    public static final List<StartPoint> BROVILLE_START_POINTs = new ArrayList<>();

    public static final StartPoint FOUNTAIN = addStartPoint(-402 ,1816 , "Fountain", 8);
    public static final StartPoint MUSEUM = addStartPoint(-565, -1918, "Town Hall", 8);
    public static final StartPoint VARSITY = addStartPoint(-565,1829, "Varsity Circle", 12);
    public static final StartPoint MALL = addStartPoint(-445, 1957, "Shopping Mall", 8);
    public static final StartPoint CHURCH = addStartPoint(-375,2121,"Church", 8);
    public static final StartPoint CASINO = addStartPoint(-249,2282,"Casino", 8);
    public static final StartPoint TRAINYARD = addStartPoint(-10,2218,"Trainyard", 6);
    public static final StartPoint DOCKS = addStartPoint(175,2113, "Docks", 8);
    public static final StartPoint FERRY_TERMINAL = addStartPoint(130,1805,"Ferry Terminal", 4);
    public static final StartPoint LION = addStartPoint(-42,1856,"Lion", 7);
    public static final StartPoint THE_NEEDLE = addStartPoint(-121, 1718,"The Needle", 6);
    public static final StartPoint DOWNTOWN = addStartPoint(-218,1968,"Downtown", 8);
    public static final StartPoint CARGO = addStartPoint(9,2012,"Cargo", 8);
    public static final StartPoint STADIUM = addStartPoint(-175,2121,"Stadium", 13);

    public static StartPoint getRandomStartPoint() {
        return BROVILLE_START_POINTs.get((int) (Math.random() * BROVILLE_START_POINTs.size()));
    }

    private static StartPoint addStartPoint(int x, int z, String name, int spreadRadius) {
        StartPoint pt = new StartPoint(x, z, name, spreadRadius);
        BROVILLE_START_POINTs.add(pt);
        return pt;
    }
}
