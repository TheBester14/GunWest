package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
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
        this.hp = this.player.getHp();
        this.score = new int[]{0, 0};
        
        loadImages();
    }
    
    private void drawLives(Graphics2D g2) {
        // Use player's current HP (assumed to be between 0 and 240)
        int currentHP = this.player.getHp();
        int hearts = currentHP / 60;
        int horizontalOffset = 30;
        int heartIndex = 0;
        
        for (int i = 0; i < hearts;  i++) {
            g2.drawImage(this.fullHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex += 1;
        }
        
        if (currentHP % 60 != 0) {
            g2.drawImage(this.halfHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex += 1;
        }
        
        for (int i = heartIndex; i < 4;  i++) {
            g2.drawImage(this.emptyHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex += 1;
        }
        
        String hpText = "HP: " + currentHP;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Orbitron", Font.BOLD, 35));
        g2.drawString(hpText, 250, 42);
    }
    
    private void drawGuns(Graphics2D g2) {
        // Draw weapon icons at fixed positions (HUD coordinates)
        AlphaComposite unselected = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        AlphaComposite selected = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        
        if (this.player.getCurrentWeapon() == 0) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        // Draw sniper at a fixed position (adjusted Y so it appears on-screen)
        g2.drawImage(this.sniper, 865, 10, 130, 130, null);
        
        if (this.player.getCurrentWeapon() == 1) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        g2.drawImage(this.shotgun, 1005, 10, 120, 120, null);
        
        if (this.player.getCurrentWeapon() == 2) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        g2.drawImage(this.pistol, 1135, 10, 94, 94, null);
        
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void drawScore(Graphics2D g2) {
        // Determine which score to display.
        // If the local player is alive, display its score;
        // otherwise, display the score of a remote player that is alive (if any).
        int displayScore = 0;
        if (player.getHp() > 0) {
            displayScore = player.getScore();
        } else {
            // Assume only one remote player remains alive
            for (RemotePlayer rp : gp.remotePlayers.values()) {
                if (rp.getHp() > 0) {
                    displayScore = rp.getScore();
                    break;
                }
            }
        }
        String scoreText = "SCORE: " + displayScore;
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Orbitron", Font.BOLD, 35));
        g2.drawString(scoreText, 520, 45);
    }

    
    public void draw(Graphics g) {
        // Create a copy of the Graphics2D object and reset its transform so that HUD elements
        // are drawn using fixed screen coordinates.
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setTransform(new AffineTransform());
        
        drawScore(g2);
        drawLives(g2);
        drawGuns(g2);
        
        g2.dispose();
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
