package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private MainMenu mainMenu;
    private ConnectionPage connectionPage;

    public GameFrame() {
       
        setTitle("GunWest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        gamePanel = new GamePanel();
        mainMenu = new MainMenu(this); // Pass the GameFrame to MainMenu
        connectionPage = new ConnectionPage(this); // Pass the GameFrame to ConnectionPage

        gamePanel.setMainMenu(mainMenu);

        setContentPane(mainMenu);
        
        // Frame settings
        this.setTitle("GunWest");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); 
        this.setLocationRelativeTo(null); // Centers the window on the screen
        this.setVisible(true);
        
        // Ensure the game panel gets focus so key events work
        panel.requestFocusInWindow();
        
        // When the window gains focus, ask the panel to request focus.
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
    
    public GamePanel getGamePanel() {
        return panel;

        pack();
        setSize(400, 300);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void switchToMainMenu() {
        setContentPane(mainMenu);
        revalidate();
        repaint();
    }

    // Method to switch to the connection page
    public void switchToConnectionPage() {
        setContentPane(connectionPage);
        revalidate();
        repaint();
    }


    public void switchToGamePanel(String ipAddress, String username) {
        gamePanel.setConnectionDetails(ipAddress, username);

        setContentPane(gamePanel);
        revalidate();
        repaint();

        // Start the game
        gamePanel.startGame();
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}