package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MenuPanel extends JPanel {
    private JButton instructionsButton;
    private JButton exitButton;
    private JButton connectButton;
    private JButton hostButton;
    private JLabel titleLabel;
    private GameFrame gameFrame;
    private BufferedImage backgroundImage;

    public MenuPanel(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setLayout(new BorderLayout());

        try {
            backgroundImage = ImageIO.read(new File("ressources/Images/MENUGUNWEST.png")); // Replace with your image path
        } catch (IOException e) {
            e.printStackTrace();
        }

        titleLabel = new JLabel("GunWest", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        add(titleLabel, BorderLayout.NORTH);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(4, 1, 10, 10)); // Adjusted for 4 buttons
        buttonsPanel.setOpaque(false);

        instructionsButton = new JButton("Instructions");
        customizeButton(instructionsButton);
        instructionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(gameFrame, "Utiliser WASD pour bouger, la souris pour viser et tirer (clique gauche)", "Instructions", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        exitButton = new JButton("Exit");
        customizeButton(exitButton);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        connectButton = new JButton("Connect");
        customizeButton(connectButton);
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open the ConnectionPage to enter IP and username
                gameFrame.switchToConnectionPage(); // Switch to the ConnectionPage
            }
        });

        hostButton = new JButton("Host");
        customizeButton(hostButton);
        hostButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Host(gameFrame);
            }
        });

        buttonsPanel.add(instructionsButton);
        buttonsPanel.add(connectButton);
        buttonsPanel.add(hostButton);
        buttonsPanel.add(exitButton);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buttonsPanel);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(Color.ORANGE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
