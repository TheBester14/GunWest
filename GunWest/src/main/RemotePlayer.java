package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.io.IOException;

public class RemotePlayer {
    private int x, y;
    private double angle;
    private int width = 50, height = 50;
    private BufferedImage sprite;
    private List<RemoteBullet> bullets;

    public RemotePlayer(int x, int y) {
        this.x = x;
        this.y = y;
        this.angle = 0.0;
        bullets = new ArrayList<>();
        try {
            sprite = ImageIO.read(getClass().getResource("/character/Walking1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setAngle(double angle) {
        this.angle = angle;
    }
    
    public void fireBullet(int startX, int startY, double bulletAngle) {
        bullets.add(new RemoteBullet(startX, startY, bulletAngle));
    }
    
    public void update() {
        // Update each bullet.
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
        
        // Draw bullets.
        for (RemoteBullet bullet : bullets) {
            bullet.draw(g2);
        }
    }
}
