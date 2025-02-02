package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import tile.TileManager;

public class Bullet extends Entity {
    private double angle;     
    private int speed;
    private boolean destroyed;
    private int damage;
    private BufferedImage image;
    private TileManager tileM;  
    private int ownerId; // Stores the id of the shooter.

    // Modified constructor: add ownerId as the last parameter.
    public Bullet(int startX, int startY, int speed, double angle, TileManager tileM, int damage, int ownerId) {
        this.x = startX;
        this.y = startY;
        this.speed = speed;
        this.angle = angle;
        this.width = 20;
        this.height = 20;
        this.color = Color.YELLOW;
        
        this.tileM = tileM;
        this.destroyed = false;  
        this.damage = damage;

 
        loadImages();

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

        

        // Calculate the centered position for the image
        int imageX = this.x + (this.width / 2) - (image.getWidth() / 2);
        int imageY = this.y + (this.height / 2) - (image.getHeight() / 2);

        // Draw the image centered within the rectangle
        g.drawImage(image, imageX, imageY, null);
    }



       
    

    public boolean isDestroyed() {
        return destroyed;
    }
    

    private void loadImages() {
   
    	if (this.damage == 30) {
    		this.image = setup("/projectile/Projectile1.png");
        }
    	
    	else if(this.damage == 60) {
    		 this.image = setup("/projectile/Projectile3.png");
    	}
    	
    	else if(this.damage == 240) {
   		 this.image = setup("/projectile/Projectile2.png");
    	}
    	
    	else {
    		System.out.println("yes");
    	}
    
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
