package entities;

public abstract class Entity {
	private int x;
	private int y;
	private int width;
	private int height;
	private int speed;
	private int health;
	
	public Entity(int x, int y, String path) {
		this.x = x;
		this.y = y;
	}
	
	public void move() {
		
	}
	
	public void takeDamage() {
		
	}
	
	public void draw() {
		
	}
	
	public void update() {
		
	}
}
