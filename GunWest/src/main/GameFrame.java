package main;

import javax.swing.*;

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