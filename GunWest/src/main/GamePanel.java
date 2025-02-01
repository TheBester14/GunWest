package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import tile.TileManager;
import entities.Player;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 704;
    public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    public final int tileSize = 32;
    public final int maxWorldCol = SCREEN_WIDTH / tileSize; // 40 colonnes
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize; // 22 lignes

    // Thread for game loop
    public Thread gameThread;
    public KeyHandler keyHandler = new KeyHandler(this);
    public Player player;
    public TileManager tileManager;

    public GamePanel() {

        this.player = new Player(keyHandler, "Adnane");

    	this.player = new Player(keyHandler, "Adnane");
 
        this.player = new Player(keyHandler, "Adnane");

        this.tileManager = new TileManager(this);

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
        Graphics2D g2 = (Graphics2D) g;

        // Dessiner la carte
        tileManager.draw(g2);

        // Dessiner le joueur par-dessus la carte
        player.draw(g2);

        g2.dispose();
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
        player.update();
    	
    }
	private void move() {
		// TODO Auto-generated method stub

	}

	private void checkCollision() {
		// TODO Auto-generated method stub
		
	}

}