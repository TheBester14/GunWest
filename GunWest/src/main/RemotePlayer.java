package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.IOException;
import tile.TileManager;

public class RemotePlayer {
    private int id;  // The remote player's id.
    private int x, y;
    private double angle;
    private int width = 50, height = 50;
    private BufferedImage sprite;
    private List<RemoteBullet> bullets;
    private int hp;
    private int score;
    private TileManager tileManager; // Reference for collision checks in remote bullets.
    
    // Modified constructor: accepts tileManager.
    public RemotePlayer(int x, int y, TileManager tileManager) {
        this.x = x;
        this.y = y;
        this.angle = 0.0;
        bullets = new ArrayList<>();
        this.hp = 240;
        this.score = 0;
        this.tileManager = tileManager;
        try {
            sprite = ImageIO.read(getClass().getResource("/character/Walking1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return this.id;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    public void fireBullet(int startX, int startY, double bulletAngle) {
        // Create a RemoteBullet with the tileManager reference.
        bullets.add(new RemoteBullet(startX, startY, bulletAngle, this.id, tileManager));
    }
    
    public void update() {
        for (int i = bullets.size() - 1; i >= 0; i--) {
            RemoteBullet b = bullets.get(i);
            b.update();
            if (b.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        g2.drawImage(sprite, -width / 2, -height / 2, width, height, null);
        g2.rotate(-angle);
        g2.translate(-centerX, -centerY);
        for (RemoteBullet bullet : bullets) {
            bullet.draw(g2);
        }
    }
    
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
    
    public void takeDamage(int damage) {
        hp -= damage;
        if (hp < 0) {
            hp = 0;
            score++; // Increase score for the attacker.
        }
    }
    
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getScore() {
        return score;
    }
    
    public List<RemoteBullet> getBullets() {
        return bullets;
    }
}
