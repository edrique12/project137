package application;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.util.ArrayList;

public class Main extends Application {

    private double width; 
    private double height; 

    // Game Logic Variables
    private long lastUpdateTime = 0;
    private double timerSeconds = 120.0;
    private int score = 0;
    private int health = 100;

    // Smart Jeep Obstacle
    private Jeep jeep;
    private double damageCooldown = 0.0;
    private static final double DAMAGE_COOLDOWN_DURATION = 1.5;
    
    private boolean w, a, s, d;
    private Label hudLabel;
    private Label missionLabel;
    
    // Game Objects
    private Player player;
    private DrivableMap gameMap;
    private Food currentGoal;
    private boolean isWaitingForPickup = true;
    private ArrayList<Item> items = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize Game World
        gameMap = new DrivableMap("Game_Map.png");
        width = gameMap.getImage().getWidth();
        height = gameMap.getImage().getHeight();
        
        player = new Player(width / 2, height / 2); // Center spawn
        jeep = new Jeep(gameMap);
        spawnNewGoal();

        // Spawn 1 poop and 1 sunflower
        items.add(new Item(Item.ItemType.POOP, gameMap, width, height));
        items.add(new Item(Item.ItemType.SUNFLOWER, gameMap, width, height));

        // 2. Setup the HUD (Timer & Score)
        hudLabel = new Label("Score: 0 | 02:00");
        hudLabel.setFont(new Font("Monospaced", 18));
        hudLabel.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 8;");

        missionLabel = new Label("Objective: Pick up order at a restaurant!");
        missionLabel.setFont(new Font("Arial", 16));
        missionLabel.setStyle("-fx-text-fill: #FFD700; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 0);");

        // 3. Setup Canvas
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // 4. Layout
        StackPane root = new StackPane();
        root.getChildren().addAll(canvas, hudLabel, missionLabel);
        
        root.setAlignment(hudLabel, Pos.TOP_RIGHT);
        root.setMargin(hudLabel, new Insets(10));
        
        root.setAlignment(missionLabel, Pos.TOP_LEFT);
        root.setMargin(missionLabel, new Insets(10));
        
        root.setStyle("-fx-background-color: #222222;");

        Scene scene = new Scene(root, width, height);

        // 5. Inputs
        scene.setOnKeyPressed(e -> handleKeys(e.getCode(), true));
        scene.setOnKeyReleased(e -> handleKeys(e.getCode(), false));

        // 6. Game Loop
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastUpdateTime > 0) {
                    double delta = (now - lastUpdateTime) / 1_000_000_000.0;
                    update(delta);
                    render(gc);
                }
                lastUpdateTime = now;
            }
        }.start();

        primaryStage.setTitle("UPLB Food Delivery Hero");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private void spawnNewGoal() {
        if (isWaitingForPickup) {
            Location loc = Location.getRandomEstablishment();
            currentGoal = new Food(loc.bx, loc.by, loc.rx, loc.ry, true);
        } else {
            Location loc = Location.getRandomLandmark();
            currentGoal = new Food(loc.bx, loc.by, loc.rx, loc.ry, false);
        }
    }

    private void handleKeys(KeyCode code, boolean isPressed) {
        switch (code) {
            case W -> w = isPressed;
            case A -> a = isPressed;
            case S -> s = isPressed;
            case D -> d = isPressed;
            default -> {}
        }
    }

    private void update(double delta) {
        // Timer
        if (timerSeconds > 0) {
            timerSeconds -= delta;
        } else {
            timerSeconds = 0;
        }

        // Player movement
        player.update(delta, w, a, s, d, gameMap, jeep);

        // Update items and check collisions
        for (Item item : items) {
            if (item.checkCollision(player.getX(), player.getY(), player.getSize())) {
                if (item.getType() == Item.ItemType.POOP) {
                    player.applySlow(5.0);
                } else if (item.getType() == Item.ItemType.SUNFLOWER) {
                    player.applyBoost(5.0);
                }
                item.respawn(gameMap, width, height);
            }
        }

        // Update damage cooldown timer
        if (damageCooldown > 0) {
            damageCooldown -= delta;
        }

        // Update smart Jeep + collision
        if (jeep != null) {
            jeep.update(delta, player);

            if (jeep.collidesWith(player.getX(), player.getY(), player.getSize())) {
                if (damageCooldown <= 0) {
                    health -= 10; // Collision damage
                    if (health < 0) health = 0;
                    damageCooldown = DAMAGE_COOLDOWN_DURATION;
                }
            }
        }

        // Delivery system
        if (currentGoal != null &&
                currentGoal.checkCollision(
                        player.getX(),
                        player.getY(),
                        player.getSize()
                )) {

            if (isWaitingForPickup) {

                isWaitingForPickup = false;

                missionLabel.setText(
                        "Objective: Deliver to a landmark!"
                );

                missionLabel.setStyle(
                        "-fx-text-fill: #00FF00;"
                );

            } else {

                score += 100;

                isWaitingForPickup = true;

                missionLabel.setText(
                        "Objective: Pick up next order!"
                );

                missionLabel.setStyle(
                        "-fx-text-fill: #FFD700;"
                );
            }

            spawnNewGoal();
        }
    }

    private void render(GraphicsContext gc) {
        gc.drawImage(gameMap.getImage(), 0, 0, width, height );

        // Goal marker
        currentGoal.draw(gc);

        // Draw smart Jeep
        if (jeep != null) {
            jeep.draw(gc);
        }

        // Draw items
        for (Item item : items) {
            item.draw(gc);
        }

        // Draw player
        player.draw(gc);

        int minutes = (int) timerSeconds / 60;
        int seconds = (int) timerSeconds % 60;

        hudLabel.setText(
                String.format("HP: %d | Score: %d | %02d:%02d", health, score, minutes, seconds)
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}