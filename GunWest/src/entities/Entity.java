package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Entity {
    public int x;
    public int y;
    protected int width;
    protected int height;
    protected Color color;
    protected int speed;
    protected int health;
    
    // Sprite images for various directions (if needed)
    protected BufferedImage up1, up2, down, left, right, upRight, upLeft, downRight, downLeft;
    protected BufferedImage down1, down2;
    
    public Entity() {
        // Default starting position and size
        this.x = 500;
        this.y = 500;
        this.width = 50;
        this.height = 50;
        this.color = Color.black;
    }
    
    public void takeDamage() {
        // Implement damage logic if needed.
    }
    
    /**
     * Loads an image from the given file path (from your resources).
     * @param filePath the resource path of the image.
     * @return the loaded BufferedImage, or null if loading fails.
     */
    public BufferedImage setup(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Could not obtain filePath to load image: " + filePath);
            e.printStackTrace();
        }
        return image;
    }
    
    public abstract void update();
    
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.x, this.y, this.width, this.height);
    }
}
