package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import tile.TileManager;
import entities.Bullet;
import entities.Player;

public class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 704;
    public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);
    
    public final int tileSize = 32;
    public final int maxWorldCol = SCREEN_WIDTH / tileSize; // 40 columns
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize; // 22 rows

    private Thread gameThread;
    private int myId;
    public MouseHandler mouseHandler;
    public KeyHandler keyHandler;
    public Player player;
    public TileManager tileManager;

    public UI ui;

    
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

        this.addMouseListener(mouseHandler);  // Now MouseHandler handles clicks as well
        ui = new UI(this, player);

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
    

    public void setMyId(int id) {
        this.myId = id;
    }

    public void setNetworkClient(network.Client client) {
        this.netClient = client;
        player.setNetworkSender(client);
    }
    

    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            // Pass the tileManager from this GamePanel to the RemotePlayer.
            RemotePlayer rp = new RemotePlayer(x, y, tileManager);
            rp.setId(id);  // Ensure the remote player's id is set correctly.
            remotePlayers.put(id, rp);
        }
    }
    
    public void updateRemotePlayerRotation(int id, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setAngle(angle);
        }
    }
    
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
        
        // Draw the local player only if alive.
        if (player.getHp() > 0) {
            player.draw(g2);
        }
        
        // Draw remote players only if they are alive.
        for (RemotePlayer rp : remotePlayers.values()) {
            if (rp.getHp() > 0) {
                rp.update();  // update bullet positions, etc.
                rp.draw(g2);
            }
        }
        
        // Draw the UI (which uses a fresh transform for HUD elements)
        ui.draw(g);
        
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
    public void updateGame() {
        int oldX = player.getX();
        int oldY = player.getY();
        
        player.update();
        
        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;
        
        if(netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }
        
        double currentAngle = player.getAngle();
        if(netClient != null && Math.abs(currentAngle - lastSentAngle) > 0.01) {
            netClient.sendToServer("ROTATE " + currentAngle);
            lastSentAngle = currentAngle;
        }
        

     // Local player's bullets vs. remote players.
        for (int i = player.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player.getBullets().get(i);
            Rectangle bulletRect = new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height);
            for (RemotePlayer rp : remotePlayers.values()) {
                // Only apply damage if the bullet's owner is not equal to the target's id.
                if (bulletRect.intersects(rp.getBounds()) && bullet.getOwnerId() != rp.getId()) {
                    netClient.sendToServer("DAMAGE " + rp.getId() + " 30");
                    bullet.setDestroyed(true);
                }
            }
        }

        // Remote players' bullets vs. local player.
        Rectangle localBounds = player.getBounds();
        for (RemotePlayer rp : remotePlayers.values()) {
            for (int i = rp.getBullets().size() - 1; i >= 0; i--) {
                RemoteBullet rb = rp.getBullets().get(i);
                Rectangle rbRect = new Rectangle((int)rb.getX(), (int)rb.getY(), rb.getSize(), rb.getSize());
                // Only apply damage if the bullet's owner is not the local player.
                if (rbRect.intersects(localBounds) && rb.getOwnerId() != myId) {
                    netClient.sendToServer("DAMAGE " + myId + " 30");
                    rb.setDestroyed(true);
                }
            }
        }
    }
    public void updateRemotePlayerHP(int id, int newHP) {
        if(remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setHp(newHP);
        }
    }

}
