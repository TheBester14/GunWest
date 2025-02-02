package main;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JPanel;
import entities.Player;
import tile.TileManager;

public class GamePanel extends JPanel implements Runnable {
    public static final int SCREEN_WIDTH = 1280;
    public static final int SCREEN_HEIGHT = 704;
    public static final Dimension SCREEN_SIZE = new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT);

    public final int tileSize = 32;
    public final int maxWorldCol = SCREEN_WIDTH / tileSize;
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize;

    private Thread gameThread;
    
    public MouseHandler mouseHandler;
    public KeyHandler keyHandler;
    public Player player; // local
    public TileManager tileManager;

    private network.Client netClient;
    public Map<Integer, RemotePlayer> remotePlayers;

    private double lastSentAngle;
    private int myId;
    private boolean gameOver = false;

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);
        
        // local
        player = new Player(keyHandler, mouseHandler, tileManager, "Local");
        lastSentAngle = player.getAngle();
        
        remotePlayers = new HashMap<>();
        
        setPreferredSize(SCREEN_SIZE);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(keyHandler);
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);
        
        // Optionally UI init, etc.

        // Make sure we get focus on mouse press
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                requestFocusInWindow();
            }
        });
        requestFocusInWindow();
        
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setNetworkClient(network.Client client) {
        this.netClient = client;
        player.setNetworkSender(client);
    }

    public void setMyId(int id) {
        this.myId = id;
        player.setId(id);
    }

    public void updateRemotePlayer(int id, int x, int y) {
        // If we don’t have a RemotePlayer for that ID, create one
        if (!remotePlayers.containsKey(id)) {
            RemotePlayer rp = new RemotePlayer(x, y, tileManager);
            rp.setId(id);
            remotePlayers.put(id, rp);
        }
        remotePlayers.get(id).setPosition(x, y);
    }

    public void updateRemotePlayerRotation(int id, double angle) {
        if (!remotePlayers.containsKey(id)) {
            RemotePlayer rp = new RemotePlayer(600,600, tileManager); // or default
            rp.setId(id);
            remotePlayers.put(id, rp);
        }
        remotePlayers.get(id).setAngle(angle);
    }

    public void remotePlayerBulletFired(int ownerId, int startX, int startY, double angle) {
        if (ownerId == myId) {
            // bullet is 'ours' but we show it from the server data
            player.fireBullet(startX, startY, angle);
        } else {
            if (!remotePlayers.containsKey(ownerId)) {
                RemotePlayer rp = new RemotePlayer(startX, startY, tileManager);
                rp.setId(ownerId);
                remotePlayers.put(ownerId, rp);
            }
            remotePlayers.get(ownerId).fireBullet(startX, startY, angle);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // draw tiles
        tileManager.draw((Graphics2D)g);
        
        // draw local player
        if (player.getHp() > 0) {
            player.draw(g);
        }
        
        // draw remote players
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.update();
            if (rp.getHp() > 0) {
                rp.draw(g);
            }
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double targetFPS = 60;
        double nsPerFrame = 1_000_000_000 / targetFPS;
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
        if (gameOver) return;
        
        int oldX = player.getX();
        int oldY = player.getY();
        
        // local movement/rotation, bullet logic
        player.update();
        
        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;
        if (netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }
        double currentAngle = player.getAngle();
        if (netClient != null && Math.abs(currentAngle - lastSentAngle) > 0.01) {
            netClient.sendToServer("ROTATE " + currentAngle);
            lastSentAngle = currentAngle;
        }
    }

    public void resetRoundLocally() {
        // Clear bullets etc if you want
        player.getBullets().clear();
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.getBullets().clear();
        }
        System.out.println("Round reset locally");
    }

    public void setGameOver(boolean val) {
        this.gameOver = val;
    }
}
