package application;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Location {
    public double bx, by; // Building/Visual coordinates
    public double rx, ry; // Road/Trigger coordinates
    public String name;

    public Location(String name, double bx, double by, double rx, double ry) {
        this.name = name;
        this.bx = bx;
        this.by = by;
        this.rx = rx;
        this.ry = ry;
    }

    public static List<Location> getEstablishments() {
        List<Location> list = new ArrayList<>();
        // Establishments (Red Rectangles)

        // Row 1 (Top)
        list.add(new Location("Grey Kiosk", 220, 60, 220, 140)); // Left block
        list.add(new Location("Brown Kiosk", 1060, 60, 1060, 140)); // Right block

        // Row 2 (6 Buildings)
        list.add(new Location("Blue Shop", 120, 240, 120, 340));
        list.add(new Location("Weber", 320, 240, 320, 340));
        list.add(new Location("KFC", 540, 240, 540, 340));
        list.add(new Location("McDonald's", 740, 240, 740, 340));
        list.add(new Location("Red Roof", 960, 240, 960, 340));
        list.add(new Location("Tea Shop", 1160, 240, 1160, 340));
        return list;
    }

    public static List<Location> getLandmarks() {
        List<Location> list = new ArrayList<>();
        // Landmarks (Yellow Rectangles)

        // Row 3
        list.add(new Location("Main Library", 220, 460, 220, 560)); // Access from road below
        list.add(new Location("Kwek Kwek Tower", 640, 460, 640, 560)); // Access from road below
        list.add(new Location("Carillon Tower", 1060, 460, 1060, 560)); // Access from road below

        // Row 4 (Bottom)
        list.add(new Location("Oblation", 640, 660, 640, 700)); // Access from road below
        return list;
    }


    private static final Random random = new Random();
    private static List<Location> establishmentBag = new ArrayList<>();
    private static List<Location> landmarkBag = new ArrayList<>();
    private static Location lastEstablishment = null;
    private static Location lastLandmark = null;

    public static Location getRandomEstablishment() {
        if (establishmentBag.isEmpty()) {
            establishmentBag.addAll(getEstablishments());
            java.util.Collections.shuffle(establishmentBag, random);
            // Prevent same location from spawning twice in a row when reshuffling the bag
            if (lastEstablishment != null
                    && establishmentBag.get(establishmentBag.size() - 1).name.equals(lastEstablishment.name)) {
                java.util.Collections.swap(establishmentBag, 0, establishmentBag.size() - 1);
            }
        }
        Location loc = establishmentBag.remove(establishmentBag.size() - 1);
        lastEstablishment = loc;
        return loc;
    }

    public static Location getRandomLandmark() {
        if (landmarkBag.isEmpty()) {
            landmarkBag.addAll(getLandmarks());
            java.util.Collections.shuffle(landmarkBag, random);
            // Prevent same location from spawning twice in a row when reshuffling the bag
            if (lastLandmark != null && landmarkBag.get(landmarkBag.size() - 1).name.equals(lastLandmark.name)) {
                java.util.Collections.swap(landmarkBag, 0, landmarkBag.size() - 1);
            }
        }
        Location loc = landmarkBag.remove(landmarkBag.size() - 1);
        lastLandmark = loc;
        return loc;
    }
}
