package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
class Player {
    private double x, y;
    
    
    //AI GENERATED DIMENSIONS FOR PLACEHOLDERS
    // 1. Reduced Size (10 to 15 is good for a 320x180 map)
    private final double SIZE = 20.0; 
    
    // 2. Reduced Speed (at 320px width, 6.0 is way too fast)
    private final double SPEED = 4.0; 

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void update(boolean w, boolean a, boolean s, boolean d, DrivableMap map) {
        double nextX = x;
        double nextY = y;

        if (w) nextY -= SPEED;
        if (s) nextY += SPEED;
        if (a) nextX -= SPEED;
        if (d) nextX += SPEED;

        // Collision Check: Check the center of the player
        double centerX = nextX + (SIZE / 2);
        double centerY = nextY + (SIZE / 2);

        if (map.isDrivable(centerX, centerY)) {
            this.x = nextX;
            this.y = nextY;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        
        
        //PLACEHOLDER BOXES AI GENERATED WHILE NO SPRITES
        // Draw the motorcycle as a small rectangle
        gc.setFill(Color.BLUE);
        gc.fillRect(x, y, SIZE, SIZE);
        
        // Front "Headlight" indicator (Yellow)
        gc.setFill(Color.YELLOW);
        gc.fillRect(x + (SIZE * 0.2), y, SIZE * 0.6, 2);
        
        gc.restore();
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return SIZE; }
}