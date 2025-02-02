package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private MainMenu mainMenu;
    private ConnectionPage connectionPage;
    private HostPage hostPage;

    public GameFrame() {
        setTitle("GunWest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        // Initialize the panels
        gamePanel = new GamePanel();
        mainMenu = new MainMenu(this); // Pass the GameFrame to MainMenu
        connectionPage = new ConnectionPage(this); // Pass the GameFrame to ConnectionPage
        hostPage = new HostPage(this);
        
        // Set the main menu as the initial content pane
        gamePanel.setMainMenu(mainMenu);
        setContentPane(mainMenu);

        // Frame settings
        pack();
        setSize(800, 600);
        setLocationRelativeTo(null);
        setVisible(true);

        // Ensure the game panel gets focus so key events work
        gamePanel.requestFocusInWindow();
        
        // When the window gains focus, ask the panel to request focus.
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
    }

    // Corrected method to return the gamePanel
    public GamePanel getGamePanel() {
        return gamePanel;
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

    public void switchToHostPage() {
        setContentPane(hostPage);
        revalidate();
        repaint();
    }

    public void startHosting(String port) {
        System.out.println("Starting hosting on port: " + port);
        // Add your hosting logic here (e.g., start a server)
        // For now, just switch to the game panel
        switchToGamePanel("localhost", port);
    }

    public void switchToGamePanel(String ipAddress, String port) {
        gamePanel.setConnectionDetailsHost(ipAddress, port);
        setContentPane(gamePanel);
        revalidate();
        repaint();
        gamePanel.startGame();
    }

    public static void main(String[] args) {
        new GameFrame();
    }
}
