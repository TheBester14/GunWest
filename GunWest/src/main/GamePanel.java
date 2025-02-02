package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import tile.TileManager;
import entities.Player;

public class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 704;
    public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    
    public final int tileSize = 32;
    public final int maxWorldCol = SCREEN_WIDTH / tileSize; // 40 columns
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize; // 22 rows
    private Thread gameThread;
    public MouseHandler mouseHandler;
 
    private String AdresseIP;
    private String nomUtil;

    
    
    private MainMenu mainMenu;
    // Thread for game loop
    
    public KeyHandler keyHandler = new KeyHandler();
    public Player player;
    public TileManager tileManager;

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);
        player = new Player(keyHandler, mouseHandler, tileManager, "Adnane");
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.player = new Player(keyHandler, mouseHandler, tileManager, "Adnane");
        this.tileManager = new TileManager(this);
        this.addKeyListener(keyHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseListener(mouseHandler);  // Now MouseHandler handles clicks as well
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
        player.update();
    }

	private void move() {
		// TODO Auto-generated method stub

	}

	private void checkCollision() {
		// TODO Auto-generated method stub
		
	}
	public void startGame() {
		System.out.println("Starting game with IP: " + AdresseIP + 
				" and username: " + nomUtil );
        // Reset the game state if needed
        // Start the game loop
        new Thread(this).start();
    }
	 public void setConnectionDetails(String AdresseIP, String nomUtil) {
	        this.AdresseIP = AdresseIP;
	        this.nomUtil = nomUtil;
	    }
    public void setMainMenu(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
    }


}

