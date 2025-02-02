package entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import tile.TileManager;
import network.NetworkSender;

public class Player extends Entity {
    private String name;
    protected KeyHandler keyHandler;
    protected MouseHandler mouseHandler;
    private ArrayList<Bullet> bullets;
    private TileManager tileM;
    
    private long fireDelay; 
    private long lastShot; 
    private double angle; 
    private int spriteCounter;
    private int spriteNum;
    
    // For sending network events.
    private NetworkSender networkSender;
    
    public void setNetworkSender(NetworkSender ns) {
         this.networkSender = ns;
    }
    
    public Player(KeyHandler keyHandler, MouseHandler mouseHandler, TileManager tileM, String name) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        this.tileM = tileM;
        this.name = name;
        
        this.speed = 4;
        this.bullets = new ArrayList<>();
        this.fireDelay = 200;
        this.lastShot = 0;
        this.width = 50;
        this.height = 50;
        
        loadImages();
        
        spriteCounter = 0;
        spriteNum = 1;
        angle = 0;
        
        // Set an initial position.
        this.x = 100;
        this.y = 100;
    }
    
    private void loadImages() {
        up1 = setup("/character/Walking1.png");
        up2 = setup("/character/Walking2.png");
    }
    
    @Override
    public void update() {
        int oldX = x;
        int oldY = y;
        
        boolean moving = false;
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
        
        if (moving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
        }
        
        // Check collision with tiles.
        if (collisionChecker()) {
            x = oldX;
            y = oldY;
        }
        
        // Mouse rotation.
        int mouseX = mouseHandler.getMouseX();
        int mouseY = mouseHandler.getMouseY();
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        
        double newAngle = Math.atan2(dy, dx) + Math.PI / 2;
        angle = newAngle;
        
        // *** Added shooting check ***
        if (mouseHandler.isLeftDown()) {
            shootBullet(angle);
        }
        
        // Update bullets & remove destroyed ones.
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            if (b.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }
    
    public boolean collisionChecker() {
        Rectangle playerRect = new Rectangle(x, y, width, height);
        
        for (int row = 0; row < tileM.gp.maxWorldRow; row++) {
            for (int col = 0; col < tileM.gp.maxWorldCol; col++) {
                int tileIndex = tileM.mapTileNumber[col][row];
                
                if (tileM.tile[tileIndex].collision) {
                    int tileX = col * tileM.gp.tileSize;
                    int tileY = row * tileM.gp.tileSize;
                    Rectangle tileRect = new Rectangle(
                        tileX,
                        tileY,
                        tileM.gp.tileSize,
                        tileM.gp.tileSize
                    );
                    if (playerRect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void draw(Graphics g) {
        BufferedImage baseImage = (spriteNum == 1) ? up1 : up2;
        
        Graphics2D g2 = (Graphics2D) g;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        AffineTransform oldTransform = g2.getTransform();
        
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        g2.drawImage(baseImage, -width / 2, -height / 2, width, height, null);
        g2.setTransform(oldTransform);
        
        // Draw bullets.
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }
    
    public void shootBullet(double angle) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShot >= fireDelay || lastShot == 0) {
            Bullet newBullet = new Bullet(
                x + width / 2,
                y + height / 2,
                8,
                angle,
                tileM
            );
            bullets.add(newBullet);
            lastShot = currentTime;
            // Send bullet event over the network if available.
            if (networkSender != null) {
                networkSender.sendToServer("BULLET " + (x + width / 2) + " " + (y + height / 2) + " " + angle);
            }
        }
    }
    
    public double getAngle() {
        return angle;
    }
    
    // Getters and setters for network updates.
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
