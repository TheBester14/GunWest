package entities;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	private String name;
	protected KeyHandler keyHandler;
	
	public Player(KeyHandler keyHandler, String name) {
		this.keyHandler = keyHandler;
		this.name = name;
		this.speed = 5;
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
	}
}
