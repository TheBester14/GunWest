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
    public final int maxWorldCol = SCREEN_WIDTH / tileSize; // 40 columns
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize; // 22 rows

    // Thread for game loop
    private Thread gameThread;
    
    // Input handlers and game entities
    public KeyHandler keyHandler = new KeyHandler(this);
    public MouseHandler mouseHandler = new MouseHandler();
    public Player player;
    public TileManager tileManager;

    public GamePanel() {
        // Create the player and tile manager
        this.player = new Player(keyHandler, mouseHandler, "Adnane");
        this.tileManager = new TileManager(this);

        // Set preferred size and background color
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        // Register input listeners:
        // NOTE: Use addMouseMotionListener to capture mouseMoved events.
        this.addMouseMotionListener(mouseHandler);
        this.addKeyListener(keyHandler);

        // Start the game loop thread
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw the tile map (if any)
        tileManager.draw(g2);
        
        // Draw the player (which rotates to face the mouse)
        player.draw(g2);
        
        g2.dispose();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double targetFPS = 60.0;
        double nsPerFrame = 1000000000 / targetFPS;
        double delta = 0;

        while (true) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            if (delta >= 1) {
                updateGame();
                repaint();
                delta--;
            }
        }
    }

    private void updateGame() {
        // Update game elements. Note: call player.update() only once.
        player.update();
    }
}
