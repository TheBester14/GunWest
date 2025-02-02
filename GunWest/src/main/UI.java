package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import entities.Player;

public class UI {
    GamePanel gp;
    private Player player;
    private int[] score;
    // Removed the cached hp field.
    private BufferedImage fullHeart, halfHeart, emptyHeart;
    private BufferedImage sniper, shotgun, pistol;
    
    public UI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        // Remove: this.hp = this.player.getHp();
        this.score = new int[]{0, 0};
        
        loadImages();
    }
    
    private void drawLives(Graphics g) {
        // Always retrieve the current HP from the player.
        int currentHP = player.getHp();
        int hearts = currentHP / 60;
        int horizontalOffset = 30;
        int heartIndex = 0;
        
        for (int i = 0; i < hearts;  i++) {
            g.drawImage(this.fullHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        if (currentHP % 60 != 0) {
            g.drawImage(this.halfHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        for (int i = heartIndex; i < 4;  i++) {
            g.drawImage(this.emptyHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        String hpText = "HP:  " + Integer.toString(currentHP);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Orbitron", Font.BOLD, 35));
        g.drawString(hpText, 250, 42);
    }
    
    private void drawGuns(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        AlphaComposite unselected = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        AlphaComposite selected = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        
        if (this.player.getCurrentWeapon() == 0) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        
        g2.drawImage(this.sniper, 865, -19, 130, 130, null);
        
        if (this.player.getCurrentWeapon() == 1) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        
        g2.drawImage(this.shotgun, 1005, -21, 120, 120, null);
        
        if (this.player.getCurrentWeapon() == 2) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        
        g2.drawImage(this.pistol, 1135, -5, 94, 94, null);
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void drawScore(Graphics g) {
        String scoreText = "SCORE:  " + Integer.toString(this.score[0]) + "  -  " + Integer.toString(this.score[0]);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Orbitron", Font.BOLD, 35));
        g.drawString(scoreText, 520, 45);
    }
    
    public void draw(Graphics g) {
        drawScore(g);
        drawLives(g);
        drawGuns(g);
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
    
    private void loadImages() {
        this.fullHeart = setup("/hearts/FullHeart.png");
        this.halfHeart = setup("/hearts/HalfHeart.png");
        this.emptyHeart = setup("/hearts/EmptyHeart.png");
        this.sniper = setup("/weapons/Sniper.png");
        this.shotgun = setup("/weapons/Shotgun.png");
        this.pistol = setup("/weapons/Pistol.png");
    }
}
