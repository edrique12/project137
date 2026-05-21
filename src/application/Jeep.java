package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class Jeep {
    private double x, y;
    private final double SIZE = 150.0;
    private final double SPEED = 2.5;

    private Image topImg;
    private Image downImg;
    private Image rightImg;
    private Image leftImg;
    private Image currentImg;

    private static final double[][] WAYPOINTS = {
        {220.0, 140.0},   // 0: Top-left
        {1060.0, 140.0},  // 1: Top-right
        {1060.0, 700.0},  // 2: Bottom-right
        {220.0, 700.0}    // 3: Bottom-left
    };
    private int currentWaypointIndex = 1; // Start by heading to Top-right

    public Jeep(DrivableMap map) {
        // Initialize position to the first waypoint (center at WAYPOINTS[0])
        this.x = WAYPOINTS[0][0] - SIZE / 2;
        this.y = WAYPOINTS[0][1] - SIZE / 2;

        Image sheet = new Image(getClass().getResourceAsStream("cars/collision/jeep_spreadsheet.png"));
        int sheetW = (int) sheet.getWidth();
        int sheetH = (int) sheet.getHeight();
        int frameW = sheetW / 2;  // 2 columns
        int frameH = sheetH / 2;  // 2 rows

        // Layout from image:
        // [top-left=up]   [top-right=down]
        // [bot-left=right] [bot-right=left]
        topImg   = new WritableImage(sheet.getPixelReader(), 0,      0,      frameW, frameH);
        downImg  = new WritableImage(sheet.getPixelReader(), frameW, 0,      frameW, frameH);
        rightImg = new WritableImage(sheet.getPixelReader(), 0,      frameH, frameW, frameH);
        leftImg  = new WritableImage(sheet.getPixelReader(), frameW, frameH, frameW, frameH);

        currentImg = rightImg; // Initially moving right
    }

    public void update(double delta, Player player) {
        // Get current target waypoint
        double targetX = WAYPOINTS[currentWaypointIndex][0];
        double targetY = WAYPOINTS[currentWaypointIndex][1];

        double curCenterX = x + SIZE / 2;
        double curCenterY = y + SIZE / 2;

        double dx = targetX - curCenterX;
        double dy = targetY - curCenterY;
        double distance = Math.sqrt(dx * dx + dy * dy);

        double step = SPEED;
        double ux = (distance > 0) ? dx / distance : 0;
        double uy = (distance > 0) ? dy / distance : 0;

        double nextJeepX = x + ux * step;
        double nextJeepY = y + uy * step;

        // Check if moving towards the player while colliding
        boolean currentlyColliding = collidesWith(player.getX(), player.getY(), player.getSize());
        if (currentlyColliding) {
            double currentOverlap = getOverlapArea(player.getX(), player.getY(), player.getSize(), x, y);
            double nextOverlap = getOverlapArea(player.getX(), player.getY(), player.getSize(), nextJeepX, nextJeepY);
            if (nextOverlap >= currentOverlap) {
                // Jeep stops because it is moving towards/blocking the player
                return;
            }
        }

        if (distance < step) {
            // We arrived at the waypoint
            x = targetX - SIZE / 2;
            y = targetY - SIZE / 2;
            currentWaypointIndex = (currentWaypointIndex + 1) % WAYPOINTS.length;
        } else {
            x += ux * step;
            y += uy * step;

            // Set correct sprite based on movement direction
            if (Math.abs(ux) > Math.abs(uy)) {
                if (ux > 0) {
                    currentImg = rightImg;
                } else {
                    currentImg = leftImg;
                }
            } else {
                if (uy > 0) {
                    currentImg = downImg;
                } else {
                    currentImg = topImg;
                }
            }
        }
    }

    public double getOverlapArea(double px, double py, double pSize, double jxVal, double jyVal) {
        // Tighter collision boxes for better navigation in tight spaces:
        // Jeep collision box: 70x70 centered inside 150x150 sprite
        double jColSize = 70.0;
        double jOffset = (SIZE - jColSize) / 2.0;
        double jx = jxVal + jOffset;
        double jy = jyVal + jOffset;

        // Player collision box: 60x60 centered inside 100x100 sprite
        double pColSize = 60.0;
        double pOffset = (pSize - pColSize) / 2.0;
        double gpx = px + pOffset;
        double gpy = py + pOffset;

        double overlapX = Math.max(0, Math.min(jx + jColSize, gpx + pColSize) - Math.max(jx, gpx));
        double overlapY = Math.max(0, Math.min(jy + jColSize, gpy + pColSize) - Math.max(jy, gpy));
        return overlapX * overlapY;
    }

    public boolean collidesWith(double px, double py, double pSize) {
        return getOverlapArea(px, py, pSize, this.x, this.y) > 0;
    }

    public boolean isMovingAway(double curPx, double curPy, double nextPx, double nextPy, double pSize) {
        double curOverlap = getOverlapArea(curPx, curPy, pSize, this.x, this.y);
        double nextOverlap = getOverlapArea(nextPx, nextPy, pSize, this.x, this.y);
        return nextOverlap < curOverlap;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(currentImg, x, y, SIZE, SIZE);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getSIZE() { return SIZE; }
}
