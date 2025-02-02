package entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public abstract class Entity {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected Color color;
    protected int speed;
    
    // Sprite images for various directions (if needed)
    protected BufferedImage up1, up2, down, left, right, upRight, upLeft, downRight, downLeft;
    protected BufferedImage down1, down2;
    
    public Entity() {
        // Default starting position and size
        this.x = 500;
        this.y = 500;
        this.width = 50;
        this.height = 50;
        this.color = Color.black;
    }
    
    public BufferedImage setup(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Could not obtain filePath to load image: " + filePath);
            e.printStackTrace();
        }
        return image;
    }
    
    public abstract void update();
    
    public void draw(Graphics g) {
        g.setColor(this.color);
        g.fillRect(this.x, this.y, this.width, this.height);
    }

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public BufferedImage getUp1() {
		return up1;
	}

	public void setUp1(BufferedImage up1) {
		this.up1 = up1;
	}

	public BufferedImage getUp2() {
		return up2;
	}

	public void setUp2(BufferedImage up2) {
		this.up2 = up2;
	}

	public BufferedImage getDown() {
		return down;
	}

	public void setDown(BufferedImage down) {
		this.down = down;
	}

	public BufferedImage getLeft() {
		return left;
	}

	public void setLeft(BufferedImage left) {
		this.left = left;
	}

	public BufferedImage getRight() {
		return right;
	}

	public void setRight(BufferedImage right) {
		this.right = right;
	}

	public BufferedImage getUpRight() {
		return upRight;
	}

	public void setUpRight(BufferedImage upRight) {
		this.upRight = upRight;
	}

	public BufferedImage getUpLeft() {
		return upLeft;
	}

	public void setUpLeft(BufferedImage upLeft) {
		this.upLeft = upLeft;
	}

	public BufferedImage getDownRight() {
		return downRight;
	}

	public void setDownRight(BufferedImage downRight) {
		this.downRight = downRight;
	}

	public BufferedImage getDownLeft() {
		return downLeft;
	}

	public void setDownLeft(BufferedImage downLeft) {
		this.downLeft = downLeft;
	}

	public BufferedImage getDown1() {
		return down1;
	}

	public void setDown1(BufferedImage down1) {
		this.down1 = down1;
	}

	public BufferedImage getDown2() {
		return down2;
	}

	public void setDown2(BufferedImage down2) {
		this.down2 = down2;
	}
}
