package main;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Rectangle;
import tile.TileManager;

public class RemoteBullet {
    private double x, y;
    private double angle;
    private double speed = 8;
    private int size = 10; // match local bullet size
    private int lifetime = 60; // frames
    private boolean destroyed = false;
    private int ownerId; // Owner id of the shooter.
    private TileManager tileM; // Reference to the tile manager for collision checking

    // Modified constructor: add ownerId and tileM.
    public RemoteBullet(int startX, int startY, double angle, int ownerId, TileManager tileM) {
        this.x = startX;
        this.y = startY;
        this.angle = angle;
        this.ownerId = ownerId;
        this.tileM = tileM;
    }
    
    public void update() {
        // Update position (same as local bullet)
        x += speed * Math.cos(angle - Math.PI / 2);
        y += speed * Math.sin(angle - Math.PI / 2);
        lifetime--;
        // Check for collision with tiles.
        if (checkTileCollision()) {
            destroyed = true;
        }
    }
    
    // Checks collision against the tile map in tileM.
    private boolean checkTileCollision() {
        Rectangle bulletRect = new Rectangle((int)x, (int)y, size, size);
        // Loop over the tile map using the same logic as in Bullet.
        for (int row = 0; row < tileM.gp.maxWorldRow; row++) {
            for (int col = 0; col < tileM.gp.maxWorldCol; col++) {
                int tileIndex = tileM.mapTileNumber[col][row];
                if (tileM.tile[tileIndex].collision) {
                    int tileX = col * tileM.gp.tileSize;
                    int tileY = row * tileM.gp.tileSize;
                    Rectangle tileRect = new Rectangle(tileX, tileY, tileM.gp.tileSize, tileM.gp.tileSize);
                    if (bulletRect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean isDestroyed() {
        return destroyed || lifetime <= 0;
    }
    
    public void setDestroyed(boolean d) {
        this.destroyed = d;
    }
    
    public double getX() {
        return x;
    }
    
    public int getSize() {
        return size;
    }
    
    public int getOwnerId() {
        return ownerId;
    }
    
    public void draw(Graphics2D g2) {
        g2.setColor(Color.YELLOW);
        g2.fillOval((int)x, (int)y, size, size);
    }

	public double getY() {
		// TODO Auto-generated method stub
		return y;
	}
}
