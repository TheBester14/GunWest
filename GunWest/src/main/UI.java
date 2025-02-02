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
    
    // Suppose we track two scores: [0] is local player's kills, [1] is maybe an opponent's.
    private int[] score;
    
    private BufferedImage fullHeart, halfHeart, emptyHeart;
    private BufferedImage sniper, shotgun, pistol;
    
    public UI(GamePanel gp, Player player) {
        this.gp = gp;
        this.player = player;
        // Start both scores at 0
        this.score = new int[]{0, 0}; 
        loadImages();
    }
    
    /**
     * If you want to update the scoreboard from outside (e.g., in GamePanel 
     * when you get "SCOREUPDATE <id> <newKills>"), call this method.
     */
    public void setScore(int playerIndex, int newScore) {
        if (playerIndex >= 0 && playerIndex < score.length) {
            score[playerIndex] = newScore;
        }
    }
    
    public int getScore(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < score.length) {
            return score[playerIndex];
        }
        return -1;
    }

    public void draw(Graphics g) {
        drawScore(g);
        drawLives(g);
        drawGuns(g);
    }
    
    private void drawScore(Graphics g) {
        /**
         * Corrected the second value to score[1], so you see something like:
         *  "SCORE:  <scoreOfPlayer0>  -  <scoreOfPlayer1>"
         */
        String scoreText = "SCORE:  " 
                           + score[0] + "  -  " + score[1];
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Orbitron", Font.BOLD, 35));
        g.drawString(scoreText, 520, 45);
    }
    
    private void drawLives(Graphics g) {
        // Current HP from the local player
        int currentHP = player.getHp();
        int hearts = currentHP / 60;  // each heart = 60 HP
        int horizontalOffset = 30;
        int heartIndex = 0;
        
        // Draw full hearts
        for (int i = 0; i < hearts; i++) {
            g.drawImage(fullHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        // If remainder HP means we need one half-heart
        if (currentHP % 60 != 0) {
            g.drawImage(halfHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        // Fill remaining slots with empty hearts (4 is total hearts possible if HP=240)
        for (int i = heartIndex; i < 4; i++) {
            g.drawImage(emptyHeart, horizontalOffset, 10, 45, 45, null);
            horizontalOffset += 50;
            heartIndex++;
        }
        
        // Numeric HP
        String hpText = "HP:  " + currentHP;
        g.setColor(Color.WHITE);
        g.setFont(new Font("Orbitron", Font.BOLD, 35));
        g.drawString(hpText, 250, 42);
    }
    
    private void drawGuns(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        AlphaComposite unselected = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
        AlphaComposite selected   = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
        
        // Sniper is weapon index 0
        if (player.getCurrentWeapon() == 0) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        g2.drawImage(sniper, 865, -19, 130, 130, null);

        // Shotgun is weapon index 1
        if (player.getCurrentWeapon() == 1) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        g2.drawImage(shotgun, 1005, -21, 120, 120, null);
        
        // Pistol is weapon index 2
        if (player.getCurrentWeapon() == 2) {
            g2.setComposite(selected);
        } else {
            g2.setComposite(unselected);
        }
        g2.drawImage(pistol, 1135, -5, 94, 94, null);
        
        // Reset alpha
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private void loadImages() {
        this.fullHeart = setup("/hearts/FullHeart.png");
        this.halfHeart = setup("/hearts/HalfHeart.png");
        this.emptyHeart = setup("/hearts/EmptyHeart.png");
        this.sniper = setup("/weapons/Sniper.png");
        this.shotgun = setup("/weapons/Shotgun.png");
        this.pistol = setup("/weapons/Pistol.png");
    }
    
    private BufferedImage setup(String filePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(filePath));
        } catch (IOException e) {
            System.out.println("Could not load image: " + filePath);
            e.printStackTrace();
        }
        return image;
    }
}
