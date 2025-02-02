package main;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entities.Player;

public class UI {
	GamePanel gp;
	private Player player;
	private int[] score;
	private int hp;
	private BufferedImage fullHeart, halfHeart, emptyHeart;
	private BufferedImage sniper, shotgun, pistol;
	
	public UI(GamePanel gp, Player player) {
		this.gp = gp;
		this.player = player;
		this.hp = player.getHp();
		this.score = new int[]{0, 0};
	}
	
	private void drawLives(Graphics g) {
		
	}
	
	private void drawGuns(Graphics g) {
		
	}
	
	private void drawScore(Graphics g) {
		
	}
	
	public void draw(Graphics g) {
		
	}
	
	private BufferedImage setup(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Could not obtain filePath to load image: " + filePath);
            e.printStackTrace();
        }
        return image;
    } 
}
