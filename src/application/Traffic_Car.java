package application;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.Random;

class Traffic_Car {

    private double x, y;
    private final double SIZE = 150;
    private double speed;

    private int dx;
    private int dy;

    private static final Random random = new Random();

    private Image topImg;
    private Image downImg;
    private Image rightImg;
    private Image leftImg;
    private Image currentImg;

    private static final int FRAME_SIZE = 256;

    // Angry emoji state
    private boolean showAngry = false;
    private double angryTimer = 0;
    private static final double ANGRY_DURATION = 1.5; // seconds to show emoji

    // Slow-down state after collision
    private double slowTimer = 0;
    private static final double SLOW_DURATION = 0.5;

    private static Image sheet = null;

    public Traffic_Car(double mapWidth, double mapHeight, DrivableMap map) {
        // Replace the static constant and sprite cutting:
        if (sheet == null) {
            sheet = new Image(
                Traffic_Car.class.getResourceAsStream("/application/cars/collision/jeep_spreadsheet.png")
            );
        }

        int sheetW = (int) sheet.getWidth();
        int sheetH = (int) sheet.getHeight();
        int frameW = sheetW / 2;  // 2 columns
        int frameH = sheetH / 2;  // 2 rows

        // Layout from your image:
        // [top-left=up]   [top-right=down]
        // [bot-left=right] [bot-right=left]
        topImg   = new WritableImage(sheet.getPixelReader(), 0,      0,      frameW, frameH);
        downImg  = new WritableImage(sheet.getPixelReader(), frameW, 0,      frameW, frameH);
        rightImg = new WritableImage(sheet.getPixelReader(), 0,      frameH, frameW, frameH);
        leftImg  = new WritableImage(sheet.getPixelReader(), frameW, frameH, frameW, frameH);

        // Try spawning on a drivable tile, but cap attempts to avoid infinite loop
        boolean validSpawn = false;
        int attempts = 0;

        while (!validSpawn && attempts < 200) {
            attempts++;
            int side = random.nextInt(4);

            switch (side) {
                case 0 -> { // LEFT → RIGHT
                    x = -SIZE;
                    y = random.nextDouble() * mapHeight;
                    dx = 1; dy = 0;
                    currentImg = rightImg;
                }
                case 1 -> { // RIGHT → LEFT
                    x = mapWidth;
                    y = random.nextDouble() * mapHeight;
                    dx = -1; dy = 0;
                    currentImg = leftImg;
                }
                case 2 -> { // TOP → DOWN
                    x = random.nextDouble() * mapWidth;
                    y = -SIZE;
                    dx = 0; dy = 1;
                    currentImg = downImg;
                }
                default -> { // BOTTOM → TOP
                    x = random.nextDouble() * mapWidth;
                    y = mapHeight;
                    dx = 0; dy = -1;
                    currentImg = topImg;
                }
            }

            // Check a point slightly inside the map in the direction of travel
            double checkX = x + dx * (SIZE * 2) + SIZE / 2;
            double checkY = y + dy * (SIZE * 2) + SIZE / 2;

            // Clamp check point to map bounds
            checkX = Math.max(0, Math.min(mapWidth - 1, checkX));
            checkY = Math.max(0, Math.min(mapHeight - 1, checkY));

            validSpawn = map.isDrivable(checkX, checkY);
        }

        // Fallback if no valid spawn found — just place off-screen going right
        if (!validSpawn) {
            x = -SIZE;
            y = mapHeight / 2;
            dx = 1; dy = 0;
            currentImg = rightImg;
        }

        speed = 2 + random.nextDouble() * 3;
    }

    public void update(double delta, DrivableMap map) {
        // Count down angry emoji timer
        if (showAngry) {
            angryTimer -= delta;
            if (angryTimer <= 0) {
                showAngry = false;
            }
        }

        // Count down slow timer
        if (slowTimer > 0) {
            slowTimer -= delta;
        }

        double effectiveSpeed = (slowTimer > 0) ? speed * 0.3 : speed;

        double nextX = x + dx * effectiveSpeed;
        double nextY = y + dy * effectiveSpeed;

        double centerX = nextX + SIZE / 2;
        double centerY = nextY + SIZE / 2;

        // Always move — ignore buildings (only stop if truly off-map)
        if (centerX >= 0 && centerY >= 0) {
            x = nextX;
            y = nextY;
        }
    }

    // Call this from Main when collision with player is detected
    public void onPlayerCollision() {
        showAngry = true;
        angryTimer = ANGRY_DURATION;
        slowTimer = SLOW_DURATION;
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(currentImg, x, y, SIZE, SIZE);

        // Draw angry emoji above car
        if (showAngry) {
            gc.setFont(new Font(28));
            gc.fillText("😡", x + SIZE / 2 - 14, y - 8);
        }
    }

    public boolean collides(Player player) {
        return x < player.getX() + player.getSize()
                && x + SIZE > player.getX()
                && y < player.getY() + player.getSize()
                && y + SIZE > player.getY();
    }

    public boolean isOffScreen(double mapWidth, double mapHeight) {
        return x < -200
                || x > mapWidth + 200
                || y < -200
                || y > mapHeight + 200;
    }
}