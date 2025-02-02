package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import tile.TileManager;

public class Bullet extends Entity {
    private double angle;     
    private int speed;
    private boolean destroyed;
    private int damage;
    
    private TileManager tileM;  
    private int ownerId; // Stores the id of the shooter.

    // Modified constructor: add ownerId as the last parameter.
    public Bullet(int startX, int startY, int speed, double angle, TileManager tileM, int damage, int ownerId) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.angle = angle;
        this.width = 10;
        this.height = 10;
        this.color = Color.YELLOW;
        
        this.tileM = tileM;
        this.destroyed = false;  
        this.damage = damage;
        this.ownerId = ownerId;
    }
    
    @Override
    public void update() {
        x += (int)(speed * Math.cos(angle - Math.PI / 2));
        y += (int)(speed * Math.sin(angle - Math.PI / 2));
        
        if (checkTileCollision()) {
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
    
    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
    
    public int getDamage() {
        return damage;
    }
    
    // Returns the owner's id.
    public int getOwnerId() {
        return ownerId;
    }
    
    private boolean checkTileCollision() {
        Rectangle bulletRect = new Rectangle(x, y, width, height);
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
}
