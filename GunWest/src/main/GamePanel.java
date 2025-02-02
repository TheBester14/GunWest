package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
    
    // For "Game Over" logic:
    private boolean gameOver = false;
    private int winnerId = -1;
    private String winnerName = "";

    // The method the client calls when a winner is declared
    public void setGameOver(boolean isOver, int winnerId, String winnerName) {
        this.gameOver = isOver;
        this.winnerId = winnerId;
        this.winnerName = winnerName;
    }

    private network.Client netClient;
    public Map<Integer, RemotePlayer> remotePlayers;

    private double lastSentAngle;
    private int myId; // local player's ID

    public GamePanel() {
        keyHandler = new KeyHandler();
        mouseHandler = new MouseHandler();
        tileManager = new TileManager(this);

        // local player name is initially "", set by client
        player = new Player(keyHandler, mouseHandler, tileManager, "", this);
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
        player.setId(id);
    }

    // Called by the client when receiving UPDATE <id> <x> <y>
    public void updateRemotePlayer(int id, int x, int y) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setPosition(x, y);
        } else {
            RemotePlayer rp = new RemotePlayer(x, y, tileManager);
            rp.setId(id);
            remotePlayers.put(id, rp);
        }
    }

    // Called by the client when receiving ROTATE <id> <angle>
    public void updateRemotePlayerRotation(int id, double angle) {
        if (remotePlayers.containsKey(id)) {
            remotePlayers.get(id).setAngle(angle);
        }
    }

    // Called by the client when receiving BULLET <ownerId> <startX> <startY> <angle>
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

        // draw local player
        if (player.getHp() > 0) {
            player.draw(g2);
        }
        ui.draw(g);

        // draw remote players
        for (RemotePlayer rp : remotePlayers.values()) {
            rp.update(); 
            if (rp.getHp() > 0) {
                rp.draw(g2);
            }
        }

        // If gameOver, show big overlay
        if (gameOver) {
            g2.setColor(new Color(0, 0, 0, 150));
            g2.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 50));

            // Show the actual winner's name
            String winnerText = winnerName + " WON!";

            // Center it
            int textWidth  = g2.getFontMetrics().stringWidth(winnerText);
            int textHeight = g2.getFontMetrics().getAscent();
            int centerX = (SCREEN_WIDTH - textWidth) / 2;
            int centerY = (SCREEN_HEIGHT - textHeight) / 2;

            g2.drawString(winnerText, centerX, centerY);
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

    // Audio 
    Sound music = new Sound();
    Sound soundEffect = new Sound();
    public void playMusic(int i) {
        music.setFile(i);
        music.play();
        music.loop();
    }
    
    public void stopMusic() {
        music.stop();
    }
    
    public void playSE(int i ) {
        soundEffect.setFile(i);
        soundEffect.play();
    }

    private void updateGame() {
        // If the game is over, skip logic
        if (gameOver) {
            return;
        }

        int oldX = player.getX();
        int oldY = player.getY();

        player.update();

        int dx = player.getX() - oldX;
        int dy = player.getY() - oldY;

        // Send movement if changed
        if (netClient != null && (dx != 0 || dy != 0)) {
            netClient.sendToServer("MOVE " + dx + " " + dy);
        }

        double currentAngle = player.getAngle();
        if (netClient != null && Math.abs(currentAngle - lastSentAngle) > 0.01) {
            netClient.sendToServer("ROTATE " + currentAngle);
            lastSentAngle = currentAngle;
        }

        // local bullets vs remote players
        for (int i = player.getBullets().size() - 1; i >= 0; i--) {
            Bullet bullet = player.getBullets().get(i);
            Rectangle bulletRect = new Rectangle(bullet.x, bullet.y, bullet.width, bullet.height);
            for (RemotePlayer rp : remotePlayers.values()) {
                if (bulletRect.intersects(rp.getBounds()) && bullet.getOwnerId() != rp.getId()) {
                    netClient.sendToServer("DAMAGE " 
                            + rp.getId() + " "          
                            + bullet.getDamage() + " " 
                            + bullet.getOwnerId()      
                    );
                    bullet.setDestroyed(true);
                }
            }
        }
        player.getBullets().removeIf(Bullet::isDestroyed);

        // remote bullets vs local player
        Rectangle localBounds = player.getBounds();
        for (RemotePlayer rp : remotePlayers.values()) {
            for (int i = rp.getBullets().size() - 1; i >= 0; i--) {
                RemoteBullet rb = rp.getBullets().get(i);
                Rectangle rbRect = new Rectangle((int)rb.getX(), (int)rb.getY(), rb.getSize(), rb.getSize());
                if (rbRect.intersects(localBounds) && rb.getOwnerId() != myId) {
                    netClient.sendToServer("DAMAGE " 
                            + myId + " "      
                            + 30 + " "        
                            + rb.getOwnerId() 
                    );
                    rb.setDestroyed(true);
                }
            }
            rp.getBullets().removeIf(RemoteBullet::isDestroyed);
        }
    }
}
