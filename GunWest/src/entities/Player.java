package entities;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import tile.TileManager;

public class Player extends Entity {
    private String name;
    private KeyHandler keyHandler;
    private MouseHandler mouseHandler;
    private ArrayList<Bullet> bullets;
    private TileManager tileM;
    private long fireDelay; 
    private long lastShot; 
    private double angle; 
    private int spriteCounter;
    private int spriteNum;
    private int hp;
    private BufferedImage up1, up2;

    public Player(KeyHandler keyHandler, MouseHandler mouseHandler, TileManager tileM, String name) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        this.tileM = tileM;
        this.name = name;
        
        this.speed = 4;
        this.bullets = new ArrayList<>();
        this.fireDelay = 200;
        this.lastShot = 0;
        
        this.width = 50;
        this.height = 50;
        this.hp = 100;
        
        loadImages();
        
        this.spriteCounter = 0;
        this.spriteNum = 1;
        this.angle = 0;
    }
    
    private void loadImages() {
        this.up1 = setup("/character/Walking1.png");
        this.up2 = setup("/character/Walking2.png");
    }
    
    @Override
    public void update() {
        int oldX = x;
        int oldY = y;
        
        boolean moving = false;
        if (keyHandler.upPressed) {
            y -= speed;
            moving = true;
        }
        if (keyHandler.downPressed) {
            y += speed;
            moving = true;
        }
        if (keyHandler.leftPressed) {
            x -= speed;
            moving = true;
        }
        if (keyHandler.rightPressed) {
            x += speed;
            moving = true;
        }
        
        if (moving) {
            spriteCounter++;
            if (spriteCounter > 10) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        } else {
            spriteNum = 1;
        }
        
        // Check collision with tiles
        if (collisionChecker()) {
            x = oldX;
            y = oldY;
        }
        
        // Mouse rotation
        int mouseX = mouseHandler.getMouseX();
        int mouseY = mouseHandler.getMouseY();
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        
        angle = Math.atan2(dy, dx);
        angle += Math.PI / 2; // depending on your sprite orientation
        
        // Fire bullets if left mouse is down
        if (mouseHandler.isLeftDown()) {
            shootBullet(angle);
        }
        
        // Update bullets & remove destroyed ones
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            if (b.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }
    
    public boolean collisionChecker() {
        Rectangle playerRect = new Rectangle(x, y, width, height);
        
        for (int row = 0; row < tileM.gp.maxWorldRow; row++) {
            for (int col = 0; col < tileM.gp.maxWorldCol; col++) {
                int tileIndex = tileM.mapTileNumber[col][row];
                
                if (tileM.tile[tileIndex].collision) {
                    int tileX = col * tileM.gp.tileSize;
                    int tileY = row * tileM.gp.tileSize;
                    Rectangle tileRect = new Rectangle(
                        tileX,
                        tileY,
                        tileM.gp.tileSize,
                        tileM.gp.tileSize
                    );
                    if (playerRect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public void draw(Graphics g) {
        BufferedImage baseImage = (spriteNum == 1) ? up1 : up2;
        
        Graphics2D g2 = (Graphics2D) g;
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        AffineTransform oldTransform = g2.getTransform();
        
        g2.translate(centerX, centerY);
        g2.rotate(angle);
        
        g2.drawImage(baseImage, -width / 2, -height / 2, width, height, null);
        
        g2.setTransform(oldTransform);
        
        // Draw bullets
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }
    
    /**
     * Shoots a bullet from the player's center at the given angle.
     */
    public void shootBullet(double angle) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShot >= fireDelay || lastShot == 0) {
            // Add the bullet, passing tileM so it can check collisions
            bullets.add(new Bullet(
                x + width / 2,
                y + height / 2,
                8,
                angle,
                tileM
            ));
            lastShot = currentTime;
        }
    }
    
    public double getAngle() {
        return angle;
    }
    
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp == 0) {
        	// kill player
        }
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public KeyHandler getKeyHandler() {
		return keyHandler;
	}

	public void setKeyHandler(KeyHandler keyHandler) {
		this.keyHandler = keyHandler;
	}

	public MouseHandler getMouseHandler() {
		return mouseHandler;
	}

	public void setMouseHandler(MouseHandler mouseHandler) {
		this.mouseHandler = mouseHandler;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public void setBullets(ArrayList<Bullet> bullets) {
		this.bullets = bullets;
	}

	public TileManager getTileM() {
		return tileM;
	}

	public void setTileM(TileManager tileM) {
		this.tileM = tileM;
	}

	public long getFireDelay() {
		return fireDelay;
	}

	public void setFireDelay(long fireDelay) {
		this.fireDelay = fireDelay;
	}

	public long getLastShot() {
		return lastShot;
	}

	public void setLastShot(long lastShot) {
		this.lastShot = lastShot;
	}

	public int getSpriteCounter() {
		return spriteCounter;
	}

	public void setSpriteCounter(int spriteCounter) {
		this.spriteCounter = spriteCounter;
	}

	public int getSpriteNum() {
		return spriteNum;
	}

	public void setSpriteNum(int spriteNum) {
		this.spriteNum = spriteNum;
	}

	public int getHp() {
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}

	public BufferedImage getUp1() {
		return up1;
	}

	public void setUp1(BufferedImage up1) {
		this.up1 = up1;
	}

	public BufferedImage getUp2() {
		return up2;
	}

	public void setUp2(BufferedImage up2) {
		this.up2 = up2;
	}
}
