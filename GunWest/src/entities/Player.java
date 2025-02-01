package entities;

import java.awt.Graphics;
import java.util.ArrayList;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	private String name;
	private KeyHandler keyHandler;
	private ArrayList<Bullet> bullets;
	
	public Player(KeyHandler keyHandler, String name) {
		this.keyHandler = keyHandler;
		this.name = name;
		this.speed = 5;
		this.bullets = new ArrayList<Bullet>();
	}
	
	public void shootBullet() {
		
	}
	
	public void update() {
		if (this.keyHandler.upPressed) {
			this.y -= this.speed;
		} else if (this.keyHandler.downPressed) {
			this.y += this.speed;
		}
		
		if (this.keyHandler.rightPressed) {
			this.x += this.speed;
		} else if (this.keyHandler.leftPressed) {
			this.x -= this.speed;
		}
		
		if (this.keyHandler.spacePressed) {
			shootBullet();
		}
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		for (Bullet bullet : this.bullets) {
			bullet.draw(g);
		}
	}
}
