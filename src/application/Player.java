package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class Player {
    private double x, y;
    
    // Game size
    private final double SIZE = 100.0;
    private final double SPEED = 4.0;

    // Sprite sheet
    private Image spriteSheet;

    // Each sprite frame size inside sheet
    private final int FRAME_SIZE = 128;

    // Which frame to render
    private int frameCol = 1; // DOWN
    private int frameRow = 0;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;

        spriteSheet = new Image(getClass().getResourceAsStream("cars/player/player1_spreadsheet.png"));
    }

    public void update(boolean w, boolean a, boolean s, boolean d, DrivableMap map) {
        double nextX = x;
        double nextY = y;

        // Movement + direction
        // Diagonals
        if (w && d) {
            nextY -= SPEED;
            nextX += SPEED;

            frameCol = 0;
            frameRow = 1; // UP_RIGHT
        }

        else if (w && a) {
            nextY -= SPEED;
            nextX -= SPEED;

            frameCol = 1;
            frameRow = 1; // UP_LEFT
        }

        else if (s && d) {
            nextY += SPEED;
            nextX += SPEED;

            frameCol = 2;
            frameRow = 1; // DOWN_RIGHT
        }

        else if (s && a) {
            nextY += SPEED;
            nextX -= SPEED;

            frameCol = 3;
            frameRow = 1; // DOWN_LEFT
        }

        // Straight directions
        else if (w) {
            nextY -= SPEED;

            frameCol = 0;
            frameRow = 0; // UP
        }

        else if (s) {
            nextY += SPEED;

            frameCol = 1;
            frameRow = 0; // DOWN
        }

        else if (d) {
            nextX += SPEED;

            frameCol = 2;
            frameRow = 0; // RIGHT
        }

        else if (a) {
            nextX -= SPEED;

            frameCol = 3;
            frameRow = 0; // LEFT
        }

        // Collision Check: Check the center of the player
        double centerX = nextX + (SIZE / 2);
        double centerY = nextY + (SIZE / 2);

        if (map.isDrivable(centerX, centerY)) {
            this.x = nextX;
            this.y = nextY;
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(
                spriteSheet,

                // source x/y inside sprite sheet
                frameCol * FRAME_SIZE,
                frameRow * FRAME_SIZE,

                // source width/height
                FRAME_SIZE,
                FRAME_SIZE,

                // destination x/y on screen
                x,
                y,

                // render size in game
                SIZE,
                SIZE
        );
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getSize() { return SIZE; }
}