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
    public final int maxWorldCol = SCREEN_WIDTH / tileSize;
    public final int maxWorldRow = SCREEN_HEIGHT / tileSize;

    private Thread gameThread;

    public MouseHandler mouseHandler;
    public KeyHandler keyHandler;
    public Player player;
    public TileManager tileManager;
    public UI ui;

    private network.Client netClient;
    public Map<Integer, RemotePlayer> remotePlayers;

    private double lastSentAngle;
    private int myId; // local player's ID

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);
        player = new Player(keyHandler, mouseHandler, tileManager, "Adnane");
        lastSentAngle = player.getAngle();

        remotePlayers = new HashMap<>();

        setPreferredSize(SCREEN_SIZE);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(keyHandler);
        addMouseMotionListener(mouseHandler);
        addMouseListener(mouseHandler);

        ui = new UI(this, player);

        // Focus logic
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
        // Optionally, if you want Player to have the same id:
        player.setId(id);
    }

    // Called by the client when receiving an UPDATE x,y
    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            // Pass tileManager so remote bullets also do tile collisions
            RemotePlayer rp = new RemotePlayer(x, y, tileManager);
            rp.setId(id);
            remotePlayers.put(id, rp);
        }
    }

    // Called by the client when receiving ROTATE
    public void updateRemotePlayerRotation(int id, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setAngle(angle);
        }
    }

    // Called by the client when receiving a BULLET message
    public void remotePlayerBulletFired(int id, int startX, int startY, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).fireBullet(startX, startY, angle);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);

        // Draw local player only if HP>0
        if (player.getHp() > 0) {
            player.draw(g2);
        }
        ui.draw(g);

        for (RemotePlayer rp : remotePlayers.values()) {
            rp.update(); // update remote bullets, remove destroyed ones
            // Draw remote player if HP>0
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

        // 1) local bullets vs remote players
        for (int i = player.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player.getBullets().get(i);
            Rectangle bulletRect = new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height);
            for (RemotePlayer rp : remotePlayers.values()) {
                if (bulletRect.intersects(rp.getBounds()) && bullet.getOwnerId() != rp.getId()) {
                    netClient.sendToServer("DAMAGE " + rp.getId() + " " + bullet.getDamage());
                    bullet.setDestroyed(true);
                }
            }
        }
        // remove destroyed local bullets
        player.getBullets().removeIf(Bullet::isDestroyed);

        // 2) remote bullets vs local player
        Rectangle localBounds = player.getBounds();
        for (RemotePlayer rp : remotePlayers.values()) {
            // remote bullets tile collisions are in RemoteBullet.update()
            // so we just remove them if isDestroyed() is true

            for (int i = rp.getBullets().size() - 1; i >= 0; i--) {
                RemoteBullet rb = rp.getBullets().get(i);
                Rectangle rbRect = new Rectangle((int)rb.getX(), (int)rb.getY(), rb.getSize(), rb.getSize());
                if (rbRect.intersects(localBounds) && rb.getOwnerId() != myId) {
                    // e.g. always 30 damage from remote bullets
                    netClient.sendToServer("DAMAGE " + myId + " 30");
                    rb.setDestroyed(true);
                }
            }
            // remove destroyed remote bullets
            rp.getBullets().removeIf(RemoteBullet::isDestroyed);
        }
        
        
    }
    
    public void resetRoundLocally() {
        // The server already updated positions & HP.
        // We can remove bullets from local + remote 
        // so no bullets remain after the round resets.
        player.getBullets().clear();
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.getBullets().clear();
        }
        System.out.println("Round reset locally. Bullets cleared.");
    }
    
    private boolean gameOver = false;
    public void setGameOver(boolean val) {
        this.gameOver = val;
        if (val) {
            System.out.println("Game Over! No more input allowed!");
        }
    }
    
}
