package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class ConnectionPage extends JPanel {
    private JTextField ipField;
    private JTextField usernameField;
    private JButton startButton;
    private GameFrame gameFrame;
    private Image backgroundImage;

    public ConnectionPage(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(1600, 900));
        setSize(1600, 900);

        try {
            URL imagePath = getClass().getResource("/Images/MENUGUNWEST.png");
            if (imagePath == null) {
                System.err.println("Error: Image not found! Check the path.");
            } else {
                System.out.println("Image found at: " + imagePath);
                backgroundImage = new ImageIcon(imagePath).getImage();
            }
        } catch (Exception e) {
            System.err.println("Exception loading image: " + e.getMessage());
        }

        setLayout(new GridBagLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2, 10, 10));

        inputPanel.add(new JLabel("IP Address:"));
        ipField = new JTextField();
        ipField.setOpaque(false);
        inputPanel.add(ipField);

        inputPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        usernameField.setOpaque(false);
        inputPanel.add(usernameField);
        startButton = new JButton("Start");
        startButton.setPreferredSize(new Dimension(250, 60));
        startButton.setBackground(Color.ORANGE);
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setFont(new Font("Arial", Font.BOLD, 18));
        startButton.addActionListener(e -> startGame());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(250, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        add(inputPanel, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(20, 0, 10, 0);
        add(startButton, gbc);
    }

    private void startGame() {
        String ipAddress = ipField.getText();
        String username = usernameField.getText();
        if (ipAddress.isEmpty() || username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both IP address and username.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        gameFrame.switchToGamePanel(ipAddress, username);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Connection Page", getWidth() / 2 - 100, 50);
    }
}
