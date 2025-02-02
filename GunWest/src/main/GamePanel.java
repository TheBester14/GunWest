package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
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
    public KeyHandler keyHandler;
    public Player player;
    public TileManager tileManager;
    
    // For networking: a reference to the network client (if set)
    private network.Client netClient;
    
    // Remote players (other clients): mapping from player ID to a RemotePlayer
    private Map<Integer, RemotePlayer> remotePlayers;

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);
        player = new Player(keyHandler, mouseHandler, tileManager, "Adnane");

        remotePlayers = new HashMap<>();
        
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseListener(mouseHandler);  // MouseHandler handles clicks as well
        
        // When the panel is clicked, request focus so key events are captured.
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                requestFocusInWindow();
            }
        });
        
        // Request focus on startup.
        this.requestFocusInWindow();
        
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // Called by the network client to provide a reference.
    public void setNetworkClient(network.Client client) {
        this.netClient = client;
    }
    
    // Called by the network client when a remote player's position update is received.
    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            remotePlayers.put(id, new RemotePlayer(x, y));
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw the tile map
        tileManager.draw(g2);
        
        // Draw the local player (controlled by the host/client)
        player.draw(g2);
        
        // Draw remote players
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.draw(g2);
        }
        
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

    // Update the game: process local player movement and send MOVE commands when needed.
    private void updateGame() {
        int oldX = player.getX();
        int oldY = player.getY();
        
        player.update();
        
        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;
        
        // If movement occurred, send a MOVE command to the server.
        if (netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }
    }
}
