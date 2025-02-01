package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import main.GamePanel;
import main.KeyHandler;

public abstract class Entity {
	private KeyHandler keyHandler;
	private GamePanel gp;
	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;
	private int health;
	private BufferedImage up, down, left, right, upRight, upLeft, downRight, downLeft;
	
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
	
	public void update() {
		
	}
	
	public void draw(Graphics g) {
		g.setColor(Color.cyan);
		g.fillRect(500, 500, 500, 500);
//		g.drawImage(sprite, x, y, null);
	}
}
