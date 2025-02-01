package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;

public abstract class Entity {
	protected KeyHandler keyHandler;
	protected GamePanel gp;
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected int speed;
	protected int health;
	protected BufferedImage up, down, left, right, upRight, upLeft, downRight, downLeft;
	
	public Entity(GamePanel gp) {
		this.gp = gp;
		keyHandler = new KeyHandler(gp);
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
		g.setColor(Color.cyan);
		g.fillRect(500, 500, 500, 500);
//		g.drawImage(sprite, x, y, null);
	}
}
