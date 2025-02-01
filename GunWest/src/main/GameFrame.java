package main;

import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private MainMenu mainMenu;
    private ConnectionPage connectionPage;

    public GameFrame() {
        // Frame settings
        setTitle("GunWest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Create the game panel, main menu, and connection page
        gamePanel = new GamePanel();
        mainMenu = new MainMenu(this); // Pass the GameFrame to MainMenu
        connectionPage = new ConnectionPage(this); // Pass the GameFrame to ConnectionPage

        // Link the game panel to the main menu
        gamePanel.setMainMenu(mainMenu);

        // Set the main menu as the initial content pane
        setContentPane(mainMenu);

        // Pack and center the window
        pack();
        setLocationRelativeTo(null); // Centers the window on the screen
        setVisible(true);
    }

    // Method to switch to the main menu
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

    // Method to switch to the game panel
    public void switchToGamePanel(String ipAddress, String username) {
        // Set connection details in the game panel
        gamePanel.setConnectionDetails(ipAddress, username);

        // Switch to the game panel
        setContentPane(gamePanel);
        revalidate();
        repaint();

        // Start the game
        gamePanel.startGame();
    }

    public static void main(String[] args) {
        // Create and display the game frame
        new GameFrame();
    }
}