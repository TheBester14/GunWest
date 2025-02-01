package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;

public abstract class Entity {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Color color;
	protected int speed;
	protected int health;
	protected BufferedImage up, down, left, right, upRight, upLeft, downRight, downLeft;
	
	public Entity() {
		this.x = 500;
		this.y = 500;
		this.width = 50;
		this.height = 50;
		this.color = Color.black;
		setImage("");
	}
	
	private BufferedImage setImage(String path) {
		try {
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	
	public void takeDamage() {
		
	}
	
	abstract void update();
	
	public void draw(Graphics g) {
		g.setColor(this.color);
		g.fillRect(this.x, this.y, this.width, this.height);
//		g.drawImage(sprite, x, y, null);
	}
}
