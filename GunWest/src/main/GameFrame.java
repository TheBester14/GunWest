package main;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;
    private MenuPanel menuPanel;
    private CardLayout cardLayout;
    private Connexion connectionPage;
    public GameFrame() {
        this.setTitle("GunWest");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(1280, 704);
        this.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        this.setLayout(cardLayout);

        menuPanel = new MenuPanel(this);
        this.add(menuPanel, "Menu");

        gamePanel = new GamePanel();
        this.add(gamePanel, "Game");
  
        cardLayout.show(this.getContentPane(), "Menu");

        this.setVisible(true);
    }

    public void startGame() {
        cardLayout.show(this.getContentPane(), "Game");
        gamePanel.requestFocusInWindow();
        gamePanel.startGameThread();
    }
    public void switchToConnectionPage() {
        if (connectionPage == null) {
        	connectionPage = new Connexion(this);
        }
        setContentPane(connectionPage);
        revalidate();
        repaint();
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }
}