package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import tile.TileManager;

public class Bullet extends Entity {
    private double angle;       // in radians
    private int speed;
    private boolean destroyed;  // bullet removal flag
    
    private TileManager tileM;  // Reference to tile manager for collision checks

    /**
     * Constructor now requires tileM so we can check tile collisions in update().
     */
    public Bullet(int startX, int startY, int speed, double angle, TileManager tileM) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.angle = angle;
        this.width = 10;
        this.height = 10;
        this.color = Color.YELLOW;
        
        this.tileM = tileM;
        this.destroyed = false;  // bullet is alive initially
    }
    
    @Override
    public void update() {
        // 1. Move the bullet
        //    If your "base" orientation is up, you might offset angle by +/- 90°.
        //    But let's keep it consistent with the player code (angle - PI/2).
        x += (int)(speed * Math.cos(angle - Math.PI / 2));
        y += (int)(speed * Math.sin(angle - Math.PI / 2));
        
        // 2. Check collision with solid tiles
        if (checkTileCollision()) {
            // Mark this bullet as destroyed so player can remove it
            destroyed = true;
        }
    }
    
    @Override
    public void draw(Graphics g) {
        // If not destroyed, draw the bullet
        // (Up to you whether or not to skip drawing if destroyed is true)
        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
    
    /**
     * Returns true if this bullet should be removed.
     */
    public boolean isDestroyed() {
        return destroyed;
    }
    
    /**
     * Checks if the bullet's bounding box intersects any "solid" (collision=true) tile.
     */
    private boolean checkTileCollision() {
        // The bullet's bounding box
        Rectangle bulletRect = new Rectangle(x, y, width, height);
        
        // Loop over the tile map to find collisions
        for (int row = 0; row < tileM.gp.maxWorldRow; row++) {
            for (int col = 0; col < tileM.gp.maxWorldCol; col++) {
                int tileIndex = tileM.mapTileNumber[col][row];
                
                // Only check if tile has collision
                if (tileM.tile[tileIndex].collision) {
                    // Calculate tile's on-screen bounding box
                    int tileX = col * tileM.gp.tileSize;
                    int tileY = row * tileM.gp.tileSize;
                    Rectangle tileRect = new Rectangle(
                        tileX, 
                        tileY, 
                        tileM.gp.tileSize, 
                        tileM.gp.tileSize
                    );
                    
                    // Check intersection
                    if (bulletRect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

	public double getAngle() {
		return angle;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public TileManager getTileM() {
		return tileM;
	}

	public void setTileM(TileManager tileM) {
		this.tileM = tileM;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}
}
