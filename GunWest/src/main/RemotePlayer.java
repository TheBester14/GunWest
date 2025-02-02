package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
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
    private int id;
    private int x, y;
    private double angle;
    private int width = 50, height = 50;
    private BufferedImage sprite;
    private List<RemoteBullet> bullets;
    private int hp = 240; // default
    private TileManager tileM; // used for tile collisions in remote bullets
    private String name = "";

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	// Constructor that takes tileManager
    public RemotePlayer(int x, int y, TileManager tm) {
        this.x = x;
        this.y = y;
        this.angle = 0.0;
        bullets = new ArrayList<>();
        this.tileM = tm;
        try {
            sprite = ImageIO.read(getClass().getResource("/character/Walking1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Overloaded if you absolutely need it
    public RemotePlayer(int x, int y) {
        this(x, y, null);
    }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void fireBullet(int startX, int startY, double bulletAngle) {
        // pass the tileManager so the bullet can collide with tiles
        bullets.add(new RemoteBullet(startX, startY, bulletAngle, this.id, tileM));
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
    private int kills;

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }

    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        // Draw the sprite with rotation
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        g2.drawImage(sprite, -width/2, -height/2, width, height, null);
        g2.rotate(-angle);
        g2.translate(-centerX, -centerY);

        // Draw bullets
        for (RemoteBullet bullet : bullets) {
            bullet.draw(g2);
        }

        // Reset alpha if needed (e.g. 1.0f for full opacity)
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Orbitron", Font.BOLD, 17));
        // Optionally center text over the sprite. For simplicity, just offset slightly.
        g2.drawString(this.name, x + 5, y - 5);
    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    // HP for remote
    public int getHp() { return hp; }
    public void setHp(int newHp) {
        if (newHp < 0) newHp = 0;
        this.hp = newHp;
    }

    public List<RemoteBullet> getBullets() {
        return bullets;
    }
}
