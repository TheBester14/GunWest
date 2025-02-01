package entities;

import main.GamePanel;
import main.KeyHandler;

public class Player extends Entity {
	private String name;
	
	public Player(GamePanel gp, KeyHandler keyHandler, String name) {
		super(gp, keyHandler);
		this.name = name;
		this.speed = 5;
	}
	
	public void shootBullet() {
		
	}
	
	public void update() {
		System.out.println(this.keyHandler.upPressed);
		if (this.keyHandler.upPressed) {
			this.y -= this.speed;
		} else if (this.keyHandler.downPressed) {
			this.y += this.speed;
		}
	}
}
