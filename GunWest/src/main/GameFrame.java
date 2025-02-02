package main;

import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private MainMenu mainMenu;
    private ConnectionPage connectionPage;
    private HostPage hostPage;
    public GameFrame() {
       
        setTitle("GunWest");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        gamePanel = new GamePanel();
        mainMenu = new MainMenu(this); // Pass the GameFrame to MainMenu
        connectionPage = new ConnectionPage(this); // Pass the GameFrame to ConnectionPage
        hostPage = new HostPage(this);
        gamePanel.setMainMenu(mainMenu);

        setContentPane(mainMenu);
        

        pack();
        setSize(800, 600);
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