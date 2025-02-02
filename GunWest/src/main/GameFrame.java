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
    
    /** Called by MainMenuPanel when user chooses Host. */
    public void startHosting() {
        // 1) Start server in a new thread
        new Thread(() -> {
            try {
                Server server = new Server(5000);
                server.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        
        // (Optional) Wait some time for server to come up
        try { Thread.sleep(1000); } catch(InterruptedException e){}
        
        // 2) Now switch to the game panel
        switchToGamePanel(/* isHost = true */);
    }
    
    /** Called by MainMenuPanel when user chooses Join. */
    public void startJoining() {
        // Switch to game panel, but we won't start server
        switchToGamePanel(/* isHost = false */);
    }

    private void switchToGamePanel() {
        // Create the game panel
        gamePanel = new GamePanel();
        
        // Create and start the client
        network.Client client = new network.Client();
        client.setGamePanel(gamePanel);
        client.start(); // This will ask for IP, username, etc
        
        // Replace content with the gamePanel
        setContentPane(gamePanel);
        pack();
        gamePanel.requestFocusInWindow();
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
