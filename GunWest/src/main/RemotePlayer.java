package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import tile.TileManager;

public class RemotePlayer {
    private int id;
    private int x, y;
    private double angle;
    private int width = 50, height = 50;
    private BufferedImage sprite;
    private ArrayList<RemoteBullet> bullets;
    
    // Add these new fields for HP and SCORE so we can sync them
    private int hp = 240;
    private int score = 0;

    // If you want tile collisions for remote bullets, store tileM
    private TileManager tileM;

    public RemotePlayer(int x, int y, TileManager tileM) {
        this.x = x;
        this.y = y;
        this.angle = 0.0;
        this.tileM = tileM;
        bullets = new ArrayList<>();
        // load your sprite...
    }

    public void setId(int id) { this.id = id; }
    public int getId() { return id; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public double getAngle() {
        return angle;
    }

    public void fireBullet(int startX, int startY, double bulletAngle) {
        bullets.add(new RemoteBullet(startX, startY, bulletAngle, this.id, tileM));
    }

    public void update() {
        // update bullets, remove destroyed, etc...
        for (int i = bullets.size() - 1; i >= 0; i--) {
            RemoteBullet rb = bullets.get(i);
            rb.update();
            if (rb.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // draw the sprite at x,y rotated by angle...
        // then draw bullets
        for (RemoteBullet rb : bullets) {
            rb.draw(g2);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // --- Add the new HP & SCORE methods ---
    public int getHp() {
        return hp;
    }
    public void setHp(int newHp) {
        if (newHp < 0) newHp = 0;
        this.hp = newHp;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int newScore) {
        this.score = newScore;
    }

    public ArrayList<RemoteBullet> getBullets() {
        return bullets;
    }
}
