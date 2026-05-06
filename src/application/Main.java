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

public class Main extends Application {

    // Dimensions based on your map (320x180)
    private final int WIDTH = 320; 
    private final int HEIGHT = 180; 

    // Game Logic Variables
    private long lastUpdateTime = 0;
    private double timerSeconds = 120.0;
    private int score = 0;
    
    private boolean w, a, s, d;
    private Label hudLabel;
    
    // Game Objects
    private Player player;
    private DrivableMap gameMap;
    private Food currentFood;

    @Override
    public void start(Stage primaryStage) {
        // 1. Initialize Game World
        gameMap = new DrivableMap("map.png");
        player = new Player(160, 100); // Centered spawn
        currentFood = new Food(gameMap, WIDTH, HEIGHT);

        // 2. Setup the HUD (Timer & Score)
        hudLabel = new Label("Score: 0 | 02:00");
        hudLabel.setFont(new Font("Monospaced", 14));
        hudLabel.setStyle(
            "-fx-background-color: rgba(0, 0, 0, 0.7);" +
            "-fx-text-fill: white;" +
            "-fx-padding: 5 10 5 10;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: #555555;" +
            "-fx-border-radius: 8;"
        );

        // 3. Setup Canvas and Border
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        StackPane canvasBorder = new StackPane(canvas);
        canvasBorder.setStyle("-fx-border-color: #222222; -fx-border-width: 4; -fx-border-style: solid;");

        // 4. Layout
        StackPane root = new StackPane();
        root.getChildren().addAll(canvasBorder, hudLabel);
        root.setAlignment(hudLabel, Pos.TOP_CENTER);
        root.setMargin(hudLabel, new Insets(10, 0, 0, 0));
        root.setStyle("-fx-background-color: #444444;"); // Dark background outside map

        Scene scene = new Scene(root, WIDTH + 40, HEIGHT + 100);

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

        primaryStage.setTitle("Motorcycle Food Hunt");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
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
        // Timer countdown
        if (timerSeconds > 0) {
            timerSeconds -= delta;
        } else {
            timerSeconds = 0;
            hudLabel.setTextFill(Color.RED);
        }

        // Move Player
        player.update(w, a, s, d, gameMap);

        // Check for Food Collection
        if (currentFood.checkCollision(player.getX(), player.getY(), player.getSize())) {
            score += 10;
            currentFood = new Food(gameMap, WIDTH, HEIGHT);
        }
    }

    private void render(GraphicsContext gc) {
        // Draw map background
        gc.drawImage(gameMap.getImage(), 0, 0);

        // Draw Game Objects
        currentFood.draw(gc);
        player.draw(gc);

        // Update HUD Text
        int minutes = (int) timerSeconds / 60;
        int seconds = (int) timerSeconds % 60;
        hudLabel.setText(String.format("Score: %d | %02d:%02d", score, minutes, seconds));
    }

    public static void main(String[] args) {
        launch(args);
    }
}