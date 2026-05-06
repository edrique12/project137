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
        this.mapImage = new Image(getClass().getResourceAsStream(imagePath));
        // 2. Get the tool that lets us inspect pixels
        this.pixelReader = mapImage.getPixelReader();
    }

    /**
     * Checks if the given position (x,y) is on a grey pixel (drivable).
     */
    public boolean isDrivable(double x, double y) {
        // Boundary checks to prevent crashes
        if (x < 0 || y < 0 || x >= mapImage.getWidth() || y >= mapImage.getHeight()) {
            return false;
        }

        // Get the color of the exact pixel
        Color color = pixelReader.getColor((int) x, (int) y);
        
        // We look for a saturation close to zero (grey) and
        // brightness near 0.6 (this specific grey). White has brightness 1.0.
        // This is safer than checking strict RGB values.
        return color.getSaturation() < 0.1 && color.getBrightness() > 0.4 && color.getBrightness() < 0.8;
    }

    public Image getImage() {
        return mapImage;
    }
}