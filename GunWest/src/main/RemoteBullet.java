package main;

import java.awt.Graphics2D;
import java.awt.Color;

public class RemoteBullet {
    private double x, y;
    private double angle;
    private double speed = 8;
    private int size = 10; // Changed size to 10 to match local bullet
    private int lifetime = 60; // frames

    public RemoteBullet(int startX, int startY, double angle) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;
    }
    
    public void update() {
        // Use the same update formula as the local bullet:
        x += speed * Math.cos(angle - Math.PI / 2);
        y += speed * Math.sin(angle - Math.PI / 2);
        lifetime--;
    }
    
    public boolean isDestroyed() {
        return lifetime <= 0;
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW); // Use yellow to match local bullet color
        g2.fillOval((int) x, (int) y, size, size);
    }
}
