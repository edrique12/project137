package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class Food {
    private double x, y;
    private final double SIZE = 8.0; // Slightly smaller than the player
    private boolean active = true;

    public Food(DrivableMap map, int width, int height) {
        spawnRandomly(map, width, height);
    }

    private void spawnRandomly(DrivableMap map, int width, int height) {
        Random rand = new Random();
        boolean validSpot = false;

        // Try up to 100 times to find a grey pixel
        for (int i = 0; i < 100; i++) {
            double testX = rand.nextDouble() * (width - SIZE);
            double testY = rand.nextDouble() * (height - SIZE);

            if (map.isDrivable(testX + SIZE / 2, testY + SIZE / 2)) {
                this.x = testX;
                this.y = testY;
                validSpot = true;
                break;
            }
        }
        
        // If no spot found in 100 tries, default to a safe middle spot 
        // (Adjust this to a known grey coordinate on your map)
        if (!validSpot) {
            this.x = 160; 
            this.y = 100;
        }
    }

    public boolean checkCollision(double playerX, double playerY, double playerSize) {
        // Simple AABB (Axis-Aligned Bounding Box) collision
        return active && 
               playerX < x + SIZE &&
               playerX + playerSize > x &&
               playerY < y + SIZE &&
               playerY + playerSize > y;
    }

    public void collect() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }
    
    //PLACEHOLDER AI GENERATED FOR TESTING
    public void draw(GraphicsContext gc) {
        if (active) {
            gc.setFill(Color.LIMEGREEN);
            gc.fillOval(x, y, SIZE, SIZE); // Food looks like a little green circle
        }
    }
}