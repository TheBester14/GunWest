package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import tile.TileManager;

public class Bullet extends Entity {
    private double angle;     
    private int speed;
    private boolean destroyed;  
    
    private TileManager tileM;  

    public Bullet(int startX, int startY, int speed, double angle, TileManager tileM) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.angle = angle;
        this.width = 10;
        this.height = 10;
        this.color = Color.YELLOW;
        
        this.tileM = tileM;
        this.destroyed = false;  
    }
    
    @Override
    public void update() {

        x += (int)(speed * Math.cos(angle - Math.PI / 2));
        y += (int)(speed * Math.sin(angle - Math.PI / 2));
        
        if (checkTileCollision()) {
            // Mark this bullet as destroyed so player can remove it
            destroyed = true;
        }
    }
    
    @Override
    public void draw(Graphics g) {

        g.setColor(color);
        g.fillOval(x, y, width, height);
    }
    

    public boolean isDestroyed() {
        return destroyed;
    }
    
 
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
}
