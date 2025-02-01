package entities;

import java.awt.Graphics;
import java.util.ArrayList;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	private String name;
	private KeyHandler keyHandler;
	private ArrayList<Bullet> bullets;
	private int vertical;
	private int horizontal;
	private long fireDelay;
	private long lastShot;
	
	public Player(KeyHandler keyHandler, String name) {
		this.keyHandler = keyHandler;
		this.name = name;
		this.speed = 4;
		this.bullets = new ArrayList<Bullet>();
		this.vertical = 0;
		this.horizontal = 2;
		this.fireDelay = 200;
		this.lastShot = 0;
	}
	
	public void shootBullet(int horizontal, int vertical) {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - this.lastShot >= this.fireDelay || this.lastShot == 0) {
			bullets.add(new Bullet(this.x, this.y, 8, horizontal, vertical));
			this.lastShot = currentTime;
		}
	}
	
	public void update() {		
		if (this.keyHandler.upPressed) {
			this.vertical = 0;
			this.y -= this.speed;
		} else if (this.keyHandler.downPressed) {
			this.y += this.speed;
			this.vertical = 1;
		} else {
			if (this.horizontal != 2) {
				this.vertical = 2;
			}
		}
		
		if (this.keyHandler.rightPressed) {
			this.horizontal = 0;
			this.x += this.speed;
		} else if (this.keyHandler.leftPressed) {
			this.x -= this.speed;
			this.horizontal = 1;
		} else {
			if (this.vertical != 2) {
				this.horizontal = 2;
			}
		}
		
		if (this.keyHandler.spacePressed) {
			shootBullet(this.horizontal, this.vertical);
		}
		
		for (Bullet bullet : bullets) {
            bullet.update();
        }
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		for (Bullet bullet : this.bullets) {
			bullet.draw(g);
		}
	}
}
