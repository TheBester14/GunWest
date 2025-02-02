package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConnectionPage extends JPanel {
    private JTextField ipField;
    private JTextField usernameField;
    private JButton startButton;
    private GameFrame gameFrame;

    public ConnectionPage(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());

        // Create input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("IP Address:"));
        ipField = new JTextField();
        inputPanel.add(ipField);
        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        inputPanel.add(usernameField);

        // Create start button
        startButton = new JButton("Start");
        startButton.addActionListener(e -> startGame());

        // Add components to the panel
        add(inputPanel, BorderLayout.CENTER);
        add(startButton, BorderLayout.SOUTH);
    }

    private void startGame() {
        String ipAddress = ipField.getText();
        String username = usernameField.getText();

        // Validate inputs
        if (ipAddress.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both IP address and username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Switch to the game panel
        gameFrame.switchToGamePanel(ipAddress, username);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Draw background or any other custom graphics for the connection page
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Connection Page", getWidth() / 2 - 100, 50);
    }
}