package entities;

import main.GamePanel;

public class Player extends Entity {
	private String name;
	
	public Player(GamePanel gp, String name) {
		super(gp);
		this.name = name;
	}
	
	public void shootBullet() {
		
	}
}
