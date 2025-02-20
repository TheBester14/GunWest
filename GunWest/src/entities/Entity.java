package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Entity {
    public int x;
    public int y;
    public int width;
    public int height;
    protected Color color;
    protected int speed;
    
    public Entity() {
        // Default values—can be overridden by subclasses.
        this.x = 500;
        this.y = 500;
        this.width = 50;
        this.height = 50;
        this.color = Color.black;
    }
    
    /**
     * Loads an image from the resource path.
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
    
    /**
     * Default draw method (can be overridden by subclasses).
     */
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.x, this.y, this.width, this.height);
    }
    
    /**
     * Returns the bounding rectangle for collision detection.
     */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    // Getters and setters.
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
}
