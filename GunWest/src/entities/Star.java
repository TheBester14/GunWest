package entities;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

public class Star extends Entity {
	private int x;
	private int y;
	private BufferedImage starImage;
	
	public Star(int x, int y) {
		this.x = x;
		this.y =  y;
		loadImage();
	}
	
	private void loadImage() {
		this.starImage = setup("/hearts/Star.png");
	}
	
	public void draw(Graphics g) {
		g.drawImage(starImage, this.x, this.y, 40, 40, null);
	}
	
	public void update() {
		
	}
}
