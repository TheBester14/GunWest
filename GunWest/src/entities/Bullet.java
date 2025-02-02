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

    public Bullet(int startX, int startY, int speed, double angle, TileManager tileM, int damage) {
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

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}
