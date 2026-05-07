package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.util.Random;

public class Food {
    private double bx, by; // Visual/Building coordinates
    private double rx, ry; // Road/Collision coordinates
    private final double SIZE = 64.0; // Scaled up 2x
    private final double COLLISION_RANGE = 40.0; // Tight range strictly on the road!
    private boolean active = true;
    private boolean isPickup; 
    
    private static Image pickupImage;
    private static Image deliveryImage;

    public Food(double bx, double by, double rx, double ry, boolean isPickup) {
        this.bx = bx;
        this.by = by;
        this.rx = rx;
        this.ry = ry;
        this.isPickup = isPickup;
        
        if (pickupImage == null) {
            pickupImage = new Image(getClass().getResourceAsStream("Delivery_Indicator.png"));
            deliveryImage = new Image(getClass().getResourceAsStream("Food_Indicator.png"));
        }
    }

    public boolean checkCollision(double playerX, double playerY, double playerSize) {
        // Collision is strictly based on the ROAD coordinates (rx, ry) directly below the building.
        // With a tight COLLISION_RANGE (40), the player must be specifically on the road below the indicator.
        double centerX = rx;
        double centerY = ry;
        double pCenterX = playerX + playerSize / 2;
        double pCenterY = playerY + playerSize / 2;
        
        double distance = Math.sqrt(Math.pow(centerX - pCenterX, 2) + Math.pow(centerY - pCenterY, 2));
        return active && distance < COLLISION_RANGE;
    }

    public void collect() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
    
    public boolean isPickup() {
        return isPickup;
    }

    public void draw(GraphicsContext gc) {
        if (active) {
            Image img = isPickup ? pickupImage : deliveryImage;
            // Draw on the BUILDING coordinates (bx, by)
            // Offset by half size so it centers on the building
            gc.drawImage(img, bx - SIZE/2, by - SIZE/2, SIZE, SIZE);
        }
    }
}