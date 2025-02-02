package entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import main.GamePanel;
import main.KeyHandler;
import main.MouseHandler;
import main.Sound;
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
    private BufferedImage up1, up2;
    private int currentWeapon;
    private int id;
    public GamePanel gp;
    
    // For sending network events.
    private NetworkSender networkSender;
  
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public void setNetworkSender(NetworkSender ns) {
         this.networkSender = ns;
    }
    
    public Player(KeyHandler keyHandler, MouseHandler mouseHandler, TileManager tileM, String name, GamePanel gp) {
        this.keyHandler = keyHandler;
        this.mouseHandler = mouseHandler;
        this.tileM = tileM;
        this.name = name;
        
        this.speed = 1;
        this.bullets = new ArrayList<>();
        this.fireDelay = 400;
        this.lastShot = 0;
        this.width = 50;
        this.height = 50;
        this.hp = 240;
        this.gp = gp;
        loadImages();
        
        this.spriteCounter = 0;
        this.spriteNum = 1;
        this.angle = 0;
        this.currentWeapon = 2;
        
        // Set an initial position.
        this.x = 100;
        this.y = 100;
    }
    
    private void loadImages() {
    	if (this.currentWeapon ==2) {
        this.up1 = setup("/character/Walking1.png");
        this.up2 = setup("/character/Walking2.png");
        }
    	
    	else if(this.currentWeapon == 0) {
    		 this.up1 = setup("/character/PersoSniper1.png");
    	     this.up2 = setup("/character/PersoSniper1.png");
    	}
    	else if(this.currentWeapon == 1) {
   		 this.up1 = setup("/character/PersoStillShotgun.png");
   	     this.up2 = setup("/character/PersoStillShotgun.png");
    	}
    }
    private int kills;

    public int getKills() { return kills; }
    public void setKills(int kills) { this.kills = kills; }
    @Override
    public void update() {
        int oldX = this.x;
        int oldY = this.y;
     
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
        	loadImages();
        } else if (this.keyHandler.twoKey) {
        	setCurrentWeapon(1);
        	this.keyHandler.twoKey = false;
        	loadImages();
        } else if (this.keyHandler.threeKey) {
        	setCurrentWeapon(2);
        	this.keyHandler.threeKey = false;
        	loadImages();
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
        
        // Check collision with tiles.
        if (collisionChecker() || boundChecker()) {
            x = oldX;
            y = oldY;
        }
        
        // Mouse rotation.
        int mouseX = mouseHandler.getMouseX();
        int mouseY = mouseHandler.getMouseY();
        int playerCenterX = x + width / 2;
        int playerCenterY = y + height / 2;
        double dx = mouseX - playerCenterX;
        double dy = mouseY - playerCenterY;
        
        double newAngle = Math.atan2(dy, dx) + Math.PI / 2;
        angle = newAngle;

        Sound soundEffect = new Sound();
        // *** Added shooting check ***
        if (mouseHandler.isLeftDown()) {
            shootBullet(angle);
            if(this.getCurrentWeapon() == 0) {
            	soundEffect.setFile(3);
            	soundEffect.playAsync();
            	soundEffect.setVolume(0.02f);
            	soundEffect.stop();
            }
            else if(this.getCurrentWeapon() == 1) {
            	soundEffect.setFile(2);
            	soundEffect.playAsync();
            	soundEffect.setVolume(0.02f);
            	soundEffect.stop();
         }
            else if(this.getCurrentWeapon() == 2) {
            	soundEffect.setFile(1);
            	soundEffect.playAsync();
            	soundEffect.setVolume(0.02f);
            	soundEffect.stop();
         }
            else {
            	soundEffect.setFile(1);
            	soundEffect.playAsync();
            	soundEffect.setVolume(0.02f);
            	soundEffect.stop();
            
        }}
        
        // Update bullets & remove destroyed ones.
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet b = bullets.get(i);
            b.update();
            if (b.isDestroyed()) {
                bullets.remove(i);
            }
        }
    }
    
    public boolean collisionChecker() {
    	int trueX = this.x + (this.width - this.width / 2) / 2;
        int trueY = this.y + (this.height - this.height / 2) / 2;
        Rectangle playerRect = new Rectangle(trueX, trueY, this.width/2, this.height/2);
        
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
                    
                    if (tileIndex == 5 && playerRect.intersects(tileRect)) {
                        tileM.mapTileNumber[col][row] = 0;
                        this.speed += 1;
                        // star soundf
                        gp.playSE(4);
                    }
                    
                    if (playerRect.intersects(tileRect)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private boolean boundChecker() {
        int panelWidth = 1280;
        int panelHeight = 704;

        int objectWidth = this.width / 2;
        int objectHeight = this.height / 2;

        int newX = this.x + (width - objectWidth) / 2;
        int newY = this.y + (height - objectHeight) / 2;

        return (newX < 0 || newX > panelWidth - objectWidth ||
        		newY < 0 || newY > panelHeight - objectHeight);
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
        
        // Draw bullets.
        for (Bullet bullet : bullets) {
            bullet.draw(g);
        }
        
        g.setColor(Color.BLACK);
        g.setFont(new Font("Orbitron", Font.BOLD, 17));
        g.drawString(this.name, this.x - 8, this.y - 5);

    }
    
    public void shootBullet(double angle) {
        long currentTime = System.currentTimeMillis();
        int damage = 0;

        switch (this.currentWeapon) {
            case 0:
            	
                damage = 240; // e.g. sniper
                this.fireDelay = 1200;
                break;
            case 1:
                damage = 60;  // e.g. shotgun
                this.fireDelay = 650;
                break;
            case 2:
                damage = 30;  // e.g. pistol
                this.fireDelay = 400;
                break;
            default:
                damage = 60;
        }

        if (currentTime - lastShot >= fireDelay || lastShot == 0) {
            // Pass the player's ID as the last param if you have a getId() method:
            // e.g., newBullet = new Bullet(..., damage, this.getId());
            Bullet newBullet = new Bullet(
                x + width / 2,
                y + height / 2,
                8,
                angle,
                tileM,
                damage,
               this.getId() 

            );
            bullets.add(newBullet);
            lastShot = currentTime;
            
            // Send bullet event over the network
            if (networkSender != null) {
                networkSender.sendToServer("BULLET " 
                    + (x + width / 2) + " " 
                    + (y + height / 2) + " " 
                    + angle
                );
            }
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

	public int getCurrentWeapon() {
		return currentWeapon;
	}

	public void setCurrentWeapon(int currentWeapon) {
		this.currentWeapon = currentWeapon;
	}

    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}
