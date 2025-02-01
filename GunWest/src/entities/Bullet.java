package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends Entity {
	private boolean directionHorizontal;
	private boolean directionVertical;
	
	public Bullet(int x, int y, int speed, boolean directionHorizontal, boolean directionVertical) {
		this.x = x;
		this.y = y;
		this.width = 10;
		this.height = 10;
		this.color = Color.BLUE;
		this.speed = speed;
		this.directionHorizontal = directionHorizontal;
		this.directionVertical = directionVertical;
	}
	
	public void update() {
		if (this.directionVertical) {
			this.y -= this.speed;
		} else {
			this.y += this.speed;
		}
		
		if (this.keyHandler.rightPressed) {
			this.x += this.speed;
		} else if (this.keyHandler.leftPressed) {
			this.x -= this.speed;
		}
	}
}
