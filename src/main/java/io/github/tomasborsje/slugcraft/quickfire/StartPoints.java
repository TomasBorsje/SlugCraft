package io.github.tomasborsje.slugcraft.quickfire;

import java.util.ArrayList;
import java.util.List;

public class StartPoints {
    public static final List<StartPoint> BROVILLE_START_POINTS = new ArrayList<>();
    public static final List<StartPoint> BROVILLE_REMAINING_START_POINTS = new ArrayList<>();

    public static final StartPoint FOUNTAIN = addStartPoint(-402 ,1816 , "Fountain", 8);
    public static final StartPoint MUSEUM = addStartPoint(-565, 1918, "Town Hall", 8);
    public static final StartPoint VARSITY = addStartPoint(-565,1829, "Varsity Circle", 12);
    public static final StartPoint MALL = addStartPoint(-445, 1957, "Shopping Mall", 8);
    public static final StartPoint CHURCH = addStartPoint(-375,2121,"Church", 8);
    public static final StartPoint CASINO = addStartPoint(-249,2282,"Casino", 8);
    public static final StartPoint TRAINYARD = addStartPoint(-10,2218,"Trainyard", 6);
    public static final StartPoint DOCKS = addStartPoint(175,2113, "Docks", 9);
    public static final StartPoint FERRY_TERMINAL = addStartPoint(130,1805,"Ferry Terminal", 4);
    public static final StartPoint LION = addStartPoint(-42,1856,"Lion", 7);
    public static final StartPoint THE_NEEDLE = addStartPoint(-121, 1718,"The Needle", 6);
    public static final StartPoint DOWNTOWN = addStartPoint(-218,1968,"Downtown", 8);
    public static final StartPoint CARGO = addStartPoint(9,2012,"Cargo", 8);
    public static final StartPoint STADIUM = addStartPoint(-175,2121,"Stadium", 13);
    public static final StartPoint STAGE = addStartPoint(-777,1799,"Stage", 8);
    public static final StartPoint TENNIS_COURTS = addStartPoint(-1015, 1966,"Tennis Courts", 8);
    public static final StartPoint EXCAVATION_SITE = addStartPoint(-1489, 2516,"Excavation Site", 12);
    public static final StartPoint ALPINE_VILLAGE = addStartPoint(-197,2914,"Alpine Village", 8);
    public static final StartPoint FARMLAND = addStartPoint(-906,2395,"Farmland", 8);
    public static final StartPoint OUTSKIRTS = addStartPoint(-1009,2236,"Outskirts", 13);
    public static final StartPoint FOOTBALL_FIELD = addStartPoint(-896,2095,"Football Field", 8);
    public static final StartPoint CONSTRUCTION = addStartPoint(-726,2166, "Under Construction", 8);
    public static final StartPoint MILITARY_BASE = addStartPoint(906,2036,"Military Base", 8);

    public static StartPoint getRandomStartPoint() {
        // Return random from all start points
        return BROVILLE_START_POINTS.get((int) (Math.random() * BROVILLE_START_POINTS.size()));
    }
    public static StartPoint getRandomNewStartPoint() {
        // If remaining start points is empty, copy all start points to remaining start points
        if (BROVILLE_REMAINING_START_POINTS.isEmpty()) {
            BROVILLE_REMAINING_START_POINTS.addAll(BROVILLE_START_POINTS);
        }
        // Get random point and remove it from the remaining points
        int randomIndex = (int) (Math.random() * BROVILLE_REMAINING_START_POINTS.size());
        StartPoint pt = BROVILLE_REMAINING_START_POINTS.get(randomIndex);
        BROVILLE_REMAINING_START_POINTS.remove(randomIndex);
        return pt;
    }
    private static StartPoint addStartPoint(int x, int z, String name, int spreadRadius) {
        StartPoint pt = new StartPoint(x, z, name, spreadRadius);
        BROVILLE_START_POINTS.add(pt);
        return pt;
    }
}
