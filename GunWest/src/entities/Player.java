package entities;

import main.GamePanel;

public class Player extends Entity {
	private String name;
	
	public Player(GamePanel gp, String name) {
		super(gp);
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
	}
}
