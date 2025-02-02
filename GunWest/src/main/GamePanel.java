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

/**
 * Main game panel that runs the loop, draws tiles, handles local + remote players, etc.
 */
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
    public Player player;              // Our local player
    public TileManager tileManager;
    public UI ui;

    private network.Client netClient;   // For sending messages to server
    public Map<Integer, RemotePlayer> remotePlayers;

    private double lastSentAngle;
    private int myId;                  // local player's ID

    // Flag to block updates after game over
    private boolean gameOver = false;

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);

        // Our local player. 
        // IMPORTANT: we no longer spawn bullets in this player's .update() automatically.
        // We'll rely on the server's "BULLET" broadcast to keep everything in sync.
        player = new Player(keyHandler, mouseHandler, tileManager, "LocalPlayer");
        lastSentAngle = player.getAngle();

        remotePlayers = new HashMap<>();

        setPreferredSize(SCREEN_SIZE);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(keyHandler);
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);

        ui = new UI(this, player);

        // Make sure we can focus
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
        // If you want, you can pass this 'client' into your Player so that it can send "BULLET" commands:
        player.setNetworkSender(client);
    }

    public void setMyId(int id) {
        this.myId = id;
        // Give our local player the same ID
        player.setId(id);
    }

    /**
     * Called by Client when it sees a "UPDATE <id> <x> <y>" message
     */
    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            RemotePlayer rp = new RemotePlayer(x, y, tileManager);
            rp.setId(id);
            remotePlayers.put(id, rp);
        }
    }

    /**
     * Called by Client when it sees a "ROTATE <id> <angle>" message
     */
    public void updateRemotePlayerRotation(int id, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setAngle(angle);
        }
    }

    /**
     * Called by Client when it sees "BULLET <ownerId> <startX> <startY> <angle>"
     */
    public void remotePlayerBulletFired(int ownerId, int startX, int startY, double angle) {
        // If we don't already have a record for that owner, create it
        if (ownerId == myId) {
            // The bullet is 'ours' but we spawn it from the server message for consistency
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
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);

        // Draw local player if alive
        if (player.getHp() > 0) {
            player.draw(g2);
        }
        // Draw UI
        ui.draw(g);

        // Draw remote players
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.update(); // moves remote bullets, collision checks, etc.
            if (rp.getHp() > 0) {
                rp.draw(g2);
            }
        }

        g2.dispose();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double targetFPS = 60.0;
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

        // Player handles WASD for movement, etc.
        player.update(); // but we don't spawn bullets in here automatically

        // If we moved, tell the server
        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;
        if (netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }

        // If our rotation changed, tell the server
        double currentAngle = player.getAngle();
        if (netClient != null && Math.abs(currentAngle - lastSentAngle) > 0.01) {
            netClient.sendToServer("ROTATE " + currentAngle);
            lastSentAngle = currentAngle;
        }

        // --- Collision: local bullets vs remote players ---
        for (int i = player.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player.getBullets().get(i);
            Rectangle bulletRect = new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height);
            for (RemotePlayer rp : remotePlayers.values()) {
                if (bulletRect.intersects(rp.getBounds()) && bullet.getOwnerId() != rp.getId()) {
                    if (netClient != null) {
                        netClient.sendToServer("DAMAGE " + rp.getId() + " " + bullet.getDamage());
                    }
                    bullet.setDestroyed(true);
                }
            }
        }
        // remove destroyed local bullets
        player.getBullets().removeIf(Bullet::isDestroyed);

        // --- Collision: remote bullets vs local player ---
        Rectangle localBounds = player.getBounds();
        for (RemotePlayer rp : remotePlayers.values()) {
            for (int i = rp.getBullets().size() - 1; i >= 0; i--) {
                RemoteBullet rb = rp.getBullets().get(i);
                Rectangle rbRect = new Rectangle((int)rb.getX(), (int)rb.getY(), rb.getSize(), rb.getSize());
                if (rbRect.intersects(localBounds) && rb.getOwnerId() != myId) {
                    if (netClient != null) {
                        netClient.sendToServer("DAMAGE " + myId + " 30");
                    }
                    rb.setDestroyed(true);
                }
            }
            // remove destroyed remote bullets
            rp.getBullets().removeIf(RemoteBullet::isDestroyed);
        }
    }
    
    public void resetRoundLocally() {
        // The server already teleports players and resets HP.
        // Clear bullets so no leftover bullets remain.
        player.getBullets().clear();
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.getBullets().clear();
        }
        System.out.println("Round reset locally. Bullets cleared.");
    }

    public void setGameOver(boolean val) {
        this.gameOver = val;
        if (val) {
            System.out.println("Game Over! No more input allowed!");
        }
    }
}
