package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

public class HostPage extends JPanel {
    private GameFrame gameFrame;
    private Image backgroundImage;

    public HostPage(GameFrame gameFrame) {
        this.gameFrame = gameFrame;
        setPreferredSize(new Dimension(1600, 900)); // Set the size to match the main menu
        setLayout(new GridBagLayout());

        // Load the background image
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

        // Create input fields and buttons
        
        JTextField portField = new JTextField(10);
        JButton startHostingButton = new JButton("Start Hosting");
        JButton backButton = new JButton("Back to Main Menu");

        // Style the buttons
        startHostingButton.setPreferredSize(new Dimension(250, 60));
        backButton.setPreferredSize(new Dimension(250, 60));

        startHostingButton.setBackground(Color.ORANGE);
        startHostingButton.setForeground(Color.WHITE);
        backButton.setBackground(Color.ORANGE);
        backButton.setForeground(Color.WHITE);

        startHostingButton.setFocusPainted(false);
        backButton.setFocusPainted(false);

        // Add action listeners
        startHostingButton.addActionListener(e -> {
            String portText = "5000";
            if (portText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a port number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String port = portText;
                // Call a method to start hosting the game
                gameFrame.startHosting(port);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid port number. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> gameFrame.switchToMainMenu());

        // Create a panel for the form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false); // Make the panel transparent

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 10, 10);



        gbc.gridx = 1;
        formPanel.add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        formPanel.add(startHostingButton, gbc);

        gbc.gridy = 2;
        formPanel.add(backButton, gbc);

        // Add the form panel to the center of the HostPage
        add(formPanel, new GridBagConstraints());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        System.out.println("Painting HostPage Background...");

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            System.err.println("Warning: Background image is null.");
        }
    }
}