package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private GameFrame gameFrame;

    public MainMenu(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());

        // Create buttons
        JButton startButton = new JButton("Start Game");
        JButton quitButton = new JButton("Quit");

        // Add action listeners to buttons
        startButton.addActionListener(e -> gameFrame.switchToConnectionPage());
        quitButton.addActionListener(e -> System.exit(0));

        // Create a panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 1, 10, 10));
        buttonPanel.add(startButton);
        buttonPanel.add(quitButton);

        // Add button panel to the center of the main menu
        add(buttonPanel, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background or any other custom graphics for the main menu
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        g.drawString("Main Menu", getWidth() / 2 - 100, 100);
    }
}