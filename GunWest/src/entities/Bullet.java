package entities;

import java.awt.Color;
import java.awt.Graphics;

public class Bullet extends Entity {
	private int directionHorizontal;
	private int directionVertical;
	
	public Bullet(int x, int y, int speed, int directionHorizontal, int directionVertical) {
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
		if (this.directionVertical == 0) {
			this.y -= this.speed;
		} else if (this.directionVertical == 1) {
			this.y += this.speed;
		}
		
		if (this.directionHorizontal == 0) {
			this.x += this.speed;
		} else if (this.directionHorizontal == 1) {
			this.x -= this.speed;
		}
	}
}
