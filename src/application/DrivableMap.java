package application;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

//THANK YOU GEMINI FOR SUGGESTING PIXELREADER MADE MY LIFE EASIER
public class DrivableMap {
    private Image mapImage;
    private PixelReader pixelReader;

    public DrivableMap(String imagePath) {
        // 1. Load the image from the source folder
        this.mapImage = new Image(getClass().getResourceAsStream("Game_Map.png"));
        // 2. Get the tool that lets us inspect pixels
        this.pixelReader = mapImage.getPixelReader();
    }

    /**
     * Checks if the given position (x,y) is on a road pixel.
     */
    public boolean isDrivable(double x, double y) {
        // Boundary checks to prevent crashes
        if (x < 0 || y < 0 || x >= mapImage.getWidth() || y >= mapImage.getHeight()) {
            return false;
        }

        // Get the color of the exact pixel
        Color color = pixelReader.getColor((int) x, (int) y);
        
        // Road detection: Low saturation (grey/white) and moderate-to-high brightness
        return color.getSaturation() < 0.15 && color.getBrightness() > 0.35;
    }

    public Image getImage() {
        return mapImage;
    }
}