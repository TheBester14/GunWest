package main;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class MainMenu extends JPanel {
    private GameFrame gameFrame;
    private Image backgroundImage;

    public MainMenu(GameFrame gameFrame) {
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
        JButton startButton = new JButton("Connection");
        JButton hostButton = new JButton("Host");
        JButton quitButton = new JButton("Quitter");

        startButton.setPreferredSize(new Dimension(250, 60));
        quitButton.setPreferredSize(new Dimension(250, 60));
        hostButton.setPreferredSize(new Dimension(250, 60));

        startButton.setBackground(Color.ORANGE);
        startButton.setForeground(Color.WHITE);
        quitButton.setBackground(Color.ORANGE);
        quitButton.setForeground(Color.WHITE);
        hostButton.setBackground(Color.ORANGE);
        hostButton.setForeground(Color.WHITE);

        startButton.setFocusPainted(false);
        quitButton.setFocusPainted(false);
        hostButton.setFocusPainted(false);

        startButton.addActionListener(e -> gameFrame.switchToConnectionPage());
        quitButton.addActionListener(e -> System.exit(0));
        hostButton.addActionListener(e -> gameFrame.switchToHostPage());  // Switch to HostPage

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(400, 0, 10, 0);
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.add(startButton);
        buttonPanel.add(hostButton);
        buttonPanel.add(quitButton);
        add(buttonPanel, gbc);
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting Background...");

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.err.println("Warning: Background image is null.");
        }
    }
}
