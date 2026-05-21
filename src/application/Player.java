package application;

import javafx.scene.canvas  .GraphicsContext;
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

    private double slowTimer = 0.0;
    private double boostTimer = 0.0;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;

        spriteSheet = new Image(getClass().getResourceAsStream("cars/player/player1_spreadsheet.png"));
    }

    public void applySlow(double duration) {
        this.slowTimer = duration;
    }

    public void applyBoost(double duration) {
        this.boostTimer = duration;
    }

    public void update(double delta, boolean w, boolean a, boolean s, boolean d, DrivableMap map, Jeep jeep) {
        // Tick down timers
        if (slowTimer > 0) {
            slowTimer -= delta;
        }
        if (boostTimer > 0) {
            boostTimer -= delta;
        }

        // Calculate dynamic speed
        double currentSpeed = SPEED;
        if (slowTimer > 0) {
            currentSpeed *= 0.5;
        }
        if (boostTimer > 0) {
            currentSpeed *= 2.0;
        }

        double moveX = 0;
        double moveY = 0;

        // Movement + direction
        // Diagonals
        if (w && d) {
            moveY = -currentSpeed;
            moveX = currentSpeed;

            frameCol = 0;
            frameRow = 1; // UP_RIGHT
        }

        else if (w && a) {
            moveY = -currentSpeed;
            moveX = -currentSpeed;

            frameCol = 1;
            frameRow = 1; // UP_LEFT
        }

        else if (s && d) {
            moveY = currentSpeed;
            moveX = currentSpeed;

            frameCol = 2;
            frameRow = 1; // DOWN_RIGHT
        }

        else if (s && a) {
            moveY = currentSpeed;
            moveX = -currentSpeed;

            frameCol = 3;
            frameRow = 1; // DOWN_LEFT
        }

        // Straight directions
        else if (w) {
            moveY = -currentSpeed;

            frameCol = 0;
            frameRow = 0; // UP
        }

        else if (s) {
            moveY = currentSpeed;

            frameCol = 1;
            frameRow = 0; // DOWN
        }

        else if (d) {
            moveX = currentSpeed;

            frameCol = 2;
            frameRow = 0; // RIGHT
        }

        else if (a) {
            moveX = -currentSpeed;

            frameCol = 3;
            frameRow = 0; // LEFT
        }

        // Sliding collision response: test X and Y axes independently
        // 1. Test X axis movement
        if (moveX != 0) {
            double testNextX = x + moveX;
            double testCenterX = testNextX + (SIZE / 2);
            double testCenterY = y + (SIZE / 2);

            boolean roadDrivable = map.isDrivable(testCenterX, testCenterY);
            boolean allowedByJeep = true;
            if (jeep != null) {
                if (jeep.collidesWith(testNextX, y, SIZE)) {
                    allowedByJeep = jeep.isMovingAway(this.x, this.y, testNextX, y, SIZE);
                }
            }

            if (roadDrivable && allowedByJeep) {
                this.x = testNextX;
            }
        }

        // 2. Test Y axis movement
        if (moveY != 0) {
            double testNextY = y + moveY;
            double testCenterX = x + (SIZE / 2);
            double testCenterY = testNextY + (SIZE / 2);

            boolean roadDrivable = map.isDrivable(testCenterX, testCenterY);
            boolean allowedByJeep = true;
            if (jeep != null) {
                if (jeep.collidesWith(x, testNextY, SIZE)) {
                    allowedByJeep = jeep.isMovingAway(this.x, this.y, x, testNextY, SIZE);
                }
            }

            if (roadDrivable && allowedByJeep) {
                this.y = testNextY;
            }
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