package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Item {
    public enum ItemType {
        POOP,
        SUNFLOWER
    }

    private double x, y;
    private final ItemType type;
    private final double size = 40.0;
    private Image image;

    private static final Random random = new Random();

    public Item(ItemType type, DrivableMap map, double mapWidth, double mapHeight) {
        this.type = type;
        
        try {
            if (type == ItemType.POOP) {
                this.image = new Image(getClass().getResourceAsStream("poop.png"));
            } else {
                this.image = new Image(getClass().getResourceAsStream("sunflower.png"));
            }
        } catch (Exception e) {
            System.err.println("Error loading image for item " + type + ": " + e.getMessage());
        }

        respawn(map, mapWidth, mapHeight);
    }

    public void respawn(DrivableMap map, double mapWidth, double mapHeight) {
        boolean validSpawn = false;
        int attempts = 0;

        while (!validSpawn && attempts < 500) {
            attempts++;
            // Generate coordinates inside the map, keeping some margin from the edges
            double rx = 50 + random.nextDouble() * (mapWidth - 100);
            double ry = 50 + random.nextDouble() * (mapHeight - 100);

            if (map.isDrivable(rx, ry)) {
                this.x = rx;
                this.y = ry;
                validSpawn = true;
            }
        }

        // Fallback: spawn on a known drivable road location (e.g., top-left intersection) if search failed
        if (!validSpawn) {
            this.x = 220;
            this.y = 140;
        }
    }

    public boolean checkCollision(double px, double py, double pSize) {
        double pCenterX = px + pSize / 2;
        double pCenterY = py + pSize / 2;
        double distance = Math.sqrt(Math.pow(this.x - pCenterX, 2) + Math.pow(this.y - pCenterY, 2));
        
        // Item radius (20) + Player radius (50) = 70.
        // Let's use 50.0 for a snug pickup feel.
        return distance < 50.0;
    }

    public void draw(GraphicsContext gc) {
        if (image != null) {
            gc.drawImage(image, x - size / 2, y - size / 2, size, size);
        }
    }

    public ItemType getType() {
        return type;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
