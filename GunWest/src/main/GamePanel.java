package main;

import java.awt.*;
import javax.swing.*;

import entities.Player;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    public static final int SCREEN_WIDTH = 1280;   // e.g. 40 tiles * 32px
    public static final int SCREEN_HEIGHT = 704;     // e.g. 22 tiles * 32px
    public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);

    // Thread for game loop
    public Thread gameThread;
    public KeyHandler keyHandler = new KeyHandler(this);
    public Player player;

    public GamePanel() {
    	this.player = new Player(this, keyHandler, "Adnane");
    	
        // Set preferred size and background color
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);
        this.setFocusable(true); // Allow panel to receive key events
        this.addKeyListener(keyHandler);

        // Start the game loop thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    // Custom drawing method
    public void draw(Graphics g) {
        // Example drawing: display a simple string
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Hello, Game Panel!", 50, 50);
        player.draw(g);
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0; // target updates per second
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                updateGame(); 
                repaint();    
                delta--;
            }
        }
    }

    private void updateGame() {
        move();
        checkCollision();
        this.player.update();
    }

    private void move() {
        // Movement logic goes here
    }

    private void checkCollision() {
        // Collision logic goes here
    }
}
