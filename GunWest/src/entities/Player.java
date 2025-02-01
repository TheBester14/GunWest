package entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import main.KeyHandler;
import main.MouseHandler;

public class Player extends Entity {
    private String name;
    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    
    // Rotation angle in radians (player will face the mouse)
    private double angle;
    
    // Animation variables for walking (two frames)
    private int spriteCounter;
    private int spriteNum;  // toggles between 1 and 2

    public Player(KeyHandler keyHandler, MouseHandler mouseHandler, String name) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        this.name = name;
        this.speed = 5; // movement speed
        
        // Set a default size (or adjust as needed)
        this.width = 50;
        this.height = 50;
        
        loadImages();
        
        // Initialize animation states
        spriteCounter = 0;
        spriteNum = 1;
        angle = 0; // Initial angle; will be updated based on mouse position
    }
    
    /**
     * Loads the two "up" sprites from resources. These images are assumed
     * to be oriented so that the character is facing "up". The draw method
     * will rotate the image to face the mouse.
     */
    private void loadImages() {
        // Use the inherited setup() method to load images.
        up1 = setup("/character/Walking1.png");
        up2 = setup("/character/Walking2.png");
    }
    
    @Override
    public void update() {
        boolean moving = false;
        
        // Keyboard movement logic
        if (keyHandler.upPressed) {
            y -= speed;
            moving = true;
        } 
        if (keyHandler.downPressed) {
            y += speed;
            moving = true;
        }
        if (keyHandler.leftPressed) {
            x -= speed;
            moving = true;
        }
        if (keyHandler.rightPressed) {
            x += speed;
            moving = true;
        }
        
        // Animate the sprite only if moving
        if (moving) {
            spriteCounter++;
            if (spriteCounter > 10) {  // adjust for faster/slower animation
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            // If not moving, keep the first frame (idle)
            spriteNum = 1;
        }
        
        // --- Mouse rotation logic ---
        // Get mouse coordinates (relative to the panel)
        int mouseX = mouseHandler.getMouseX();
        int mouseY = mouseHandler.getMouseY();
        
        // Log the mouse coordinates to the console
        System.out.println("Mouse coordinates: (" + mouseX + ", " + mouseY + ")");
        
        // Calculate the center of the player
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;
        
        // Compute difference between mouse position and player center
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        
        // Calculate angle using atan2 (result is in radians)
        angle = Math.atan2(dy, dx);
        
        // Adjust the angle so that the sprite's default "up" orientation aligns properly.
        // For a sprite drawn facing up, add π/2.
        angle += Math.PI / 2;
    }
    
    @Override
    public void draw(Graphics g) {
        // Choose the base image based on the current animation frame
        BufferedImage baseImage = (spriteNum == 1) ? up1 : up2;
        
        Graphics2D g2 = (Graphics2D) g;
        
        // Compute the center of the player for rotation
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        // Save the current transform so we can restore it later
        AffineTransform oldTransform = g2.getTransform();
        
        // Translate to the player's center
        g2.translate(centerX, centerY);
        
        // Rotate the graphics context by the angle computed in update()
        g2.rotate(angle);
        
        // Draw the sprite so that it is centered at the origin.
        g2.drawImage(baseImage, -width / 2, -height / 2, width, height, null);
        
        // Restore the original transform
        g2.setTransform(oldTransform);
    }
    
    /**
     * Example method to shoot a bullet in the direction the player is facing.
     */
    public void shootBullet() {
        // Use the current 'angle' to create a bullet with a trajectory.
    }
}
