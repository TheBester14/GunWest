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
import network.NetworkSender;

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
    private int id;  // Added: player's id
    private BufferedImage up1, up2;
    private int currentWeapon;
    
    // For sending network events.
    private NetworkSender networkSender;
    
    public void setNetworkSender(NetworkSender ns) {
         this.networkSender = ns;
    }
    
    // New getter and setter for id.
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    
    public Player(KeyHandler keyHandler, MouseHandler mouseHandler, TileManager tileM, String name) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        this.tileM = tileM;
        this.name = name;
        
        this.speed = 4;
        this.bullets = new ArrayList<>();
        this.fireDelay = 400;
        this.lastShot = 0;
        this.width = 50;
        this.height = 50;
        this.hp = 240;
        this.currentWeapon = 2;
        
        loadImages();
        
        this.spriteCounter = 0;
        this.spriteNum = 1;
        this.angle = 0;
        
        // Set an initial position.
        this.x = 100;
        this.y = 100;
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
        if (this.keyHandler.upPressed) {
        	this.y -= this.speed;
            moving = true;
        }
        if (this.keyHandler.downPressed) {
        	this.y += this.speed;
            moving = true;
        }
        if (this.keyHandler.leftPressed) {
        	this.x -= this.speed;
            moving = true;
        }
        if (this.keyHandler.rightPressed) {
        	this.x += this.speed;
            moving = true;
        }
        
        if (this.keyHandler.oneKey) {
        	setCurrentWeapon(0);
        	this.keyHandler.oneKey = false;
        } else if (this.keyHandler.twoKey) {
        	setCurrentWeapon(1);
        	this.keyHandler.twoKey = false;
        } else if (this.keyHandler.threeKey) {
        	setCurrentWeapon(2);
        	this.keyHandler.threeKey = false;
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
        
        if (collisionChecker()) {
            x = oldX;
            y = oldY;
        }
        
        int mouseX = mouseHandler.getMouseX();
        int mouseY = mouseHandler.getMouseY();
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        
        double newAngle = Math.atan2(dy, dx) + Math.PI / 2;
        angle = newAngle;
        
        if (mouseHandler.isLeftDown()) {
            shootBullet(angle);
        }
        
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            if (b.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }
    
    private void setCurrentWeapon(int currentWeapon) {
		this.currentWeapon = currentWeapon;
		
	}

	public boolean collisionChecker() {
        Rectangle playerRect = new Rectangle(x, y, width, height);
        for (int row = 0; row < tileM.gp.maxWorldRow; row++) {
            for (int col = 0; col < tileM.gp.maxWorldCol; col++) {
                int tileIndex = tileM.mapTileNumber[col][row];
                if (tileM.tile[tileIndex].collision) {
                    int tileX = col * tileM.gp.tileSize;
                    int tileY = row * tileM.gp.tileSize;
                    Rectangle tileRect = new Rectangle(tileX, tileY, tileM.gp.tileSize, tileM.gp.tileSize);
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
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
    }
    
    public void shootBullet(double angle) {
        long currentTime = System.currentTimeMillis();
        int damage = 0;
        
        switch(this.currentWeapon) {
	        case 0:
	          damage = 240;
	          this.fireDelay = 1200;
	          break;
	        case 1:
	          damage = 60;
	          this.fireDelay = 650;
	          break;
	        case 2:
	          damage = 30;
	          this.fireDelay = 400;
	          break;
	        default:
	          damage = 60;
	      }
        
        if (currentTime - lastShot >= fireDelay || lastShot == 0) {

        	
            // Pass this player's id as ownerId.
    
            Bullet newBullet = new Bullet(
                x + width / 2,
                y + height / 2,
                8,
                angle,
                tileM,
                this.id,
                5
            );

            bullets.add(newBullet);
            lastShot = currentTime;
            if (networkSender != null) {
                networkSender.sendToServer("BULLET " + (x + width / 2) + " " + (y + height / 2) + " " + angle);
            }
        }
    }
    
    public double getAngle() {
        return angle;
    }
    
    public int getHp() {
        return hp;
    }
    
    public void setHp(int hp) {
        this.hp = hp;
    }
    
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }
    
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }

	public int getCurrentWeapon() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getScore() {
		// TODO Auto-generated method stub
		return 0;
	}
}
