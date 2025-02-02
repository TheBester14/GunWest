package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
    
    // Remote players: mapping from player ID to RemotePlayer.
    // (They are used for drawing remote players including their rotation and bullets.)
    public Map<Integer, RemotePlayer> remotePlayers;

    // For sending rotation updates.
    private double lastSentAngle;

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);
        player = new Player(keyHandler, mouseHandler, tileManager, "Adnane");
        // Set initial lastSentAngle to player's starting angle.
        lastSentAngle = player.getAngle();

        remotePlayers = new HashMap<>();
        
        this.setPreferredSize(SCREEN_SIZE);
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        this.addMouseMotionListener(mouseHandler);
        this.addMouseListener(mouseHandler);  // MouseHandler handles clicks as well
        
        // When the panel is clicked, request focus.
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });
        
        this.requestFocusInWindow();
        
        gameThread = new Thread(this);
        gameThread.start();
    }
    
    // Called by the network client to provide a reference.
    public void setNetworkClient(network.Client client) {
        this.netClient = client;
        // Also pass the network sender to the local player.
        player.setNetworkSender(client);
    }
    
    // Called by the network client when a remote player's position update is received.
    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            remotePlayers.put(id, new RemotePlayer(x, y));
        }
    }
    
    // Called by the network client when a remote player's rotation update is received.
    public void updateRemotePlayerRotation(int id, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setAngle(angle);
        }
    }
    
    // Called by the network client when a remote player fires a bullet.
    public void remotePlayerBulletFired(int id, int startX, int startY, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).fireBullet(startX, startY, angle);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw the tile map.
        tileManager.draw(g2);
        
        // Draw the local player.
        player.draw(g2);
        
        // Draw remote players.
        for(RemotePlayer rp : remotePlayers.values()) {
            rp.update();  // update bullet positions, etc.
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

    // Update the game: process local player movement and send MOVE/ROTATE commands.
    private void updateGame() {
        int oldX = player.getX();
        int oldY = player.getY();
        
        player.update();
        
        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;
        
        // If movement occurred, send a MOVE command.
        if(netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }
        
        // Check if rotation has changed significantly.
        double currentAngle = player.getAngle();
        if(netClient != null && Math.abs(currentAngle - lastSentAngle) > 0.01) {
            netClient.sendToServer("ROTATE " + currentAngle);
            lastSentAngle = currentAngle;
        }
    }
}
