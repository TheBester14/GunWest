package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import network.Client;
import network.Server;
import java.io.IOException;

public class GameFrame extends JFrame {
    private MainMenuPanel menuPanel;
    private GamePanel gamePanel;

    public GameFrame() {
        // Create a main menu panel
        menuPanel = new MainMenuPanel(this);
        menuPanel.setPreferredSize(GamePanel.SCREEN_SIZE);
        
        setTitle("GunWest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(menuPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        // Ensure menuPanel gets focus
        menuPanel.requestFocusInWindow();
        
        // If window gains focus, ask panel to request focus
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                menuPanel.requestFocusInWindow();
            }
        });
    }
    
    public void startHosting() {
        new Thread(() -> {
            try {
                Server server = new Server(5000);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        try {
            Thread.sleep(1000); // give server a moment
        } catch (InterruptedException e) {}
        
        switchToGamePanel(); 
    }

    public void startJoining() {
        switchToGamePanel(); 
    }

    private void switchToGamePanel() {
        gamePanel = new GamePanel();
        network.Client client = new network.Client();
        client.setGamePanel(gamePanel);
        client.start();
        
        setContentPane(gamePanel);
        pack();
        gamePanel.requestFocusInWindow();
    }


    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
