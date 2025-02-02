package network;

import javax.swing.JFrame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;

public class GameClientFrame extends JFrame {
    private ClientGamePanel gamePanel;
    private Client clientRef;

    public GameClientFrame(Map<Integer, PlayerState> playersMap) {
        setTitle("Multiplayer Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create and add the game panel
        gamePanel = new ClientGamePanel(playersMap);
        add(gamePanel);
        
        // Listen for WASD key presses to send movement commands.
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (clientRef == null) return;
                int key = e.getKeyCode();
                int moveAmount = 10;  // the number of pixels to move per command
                String moveCmd = null;
                if (key == KeyEvent.VK_W) {
                    moveCmd = "MOVE 0 -" + moveAmount;
                } else if (key == KeyEvent.VK_S) {
                    moveCmd = "MOVE 0 " + moveAmount;
                } else if (key == KeyEvent.VK_A) {
                    moveCmd = "MOVE -" + moveAmount + " 0";
                } else if (key == KeyEvent.VK_D) {
                    moveCmd = "MOVE " + moveAmount + " 0";
                }
                if (moveCmd != null) {
                    clientRef.sendToServer(moveCmd);
                }
            }
        });
    }

    public void setClientRef(Client client) {
        this.clientRef = client;
    }

    public void refresh() {
        gamePanel.repaint();
    }
}
