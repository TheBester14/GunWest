package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class MainMenuPanel extends JPanel implements KeyListener {
    private BufferedImage background;
    private Sound menuMusic;
    
    // The menu options
    private String[] options = { "HOST", "JOIN", "Game Instructions" };
    private int selectedIndex = 0;

    // Reference to the parent frame to switch between frames.
    private GameFrame parentFrame;
    
    public MainMenuPanel(GameFrame parentFrame) {
        this.parentFrame = parentFrame;
        setFocusable(true);
        addKeyListener(this);
        
        // Load the background image
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/character/menu_image.jpg"));
        } catch (IOException e) {
            System.out.println("Could not load background image!");
        }
        
        menuMusic = new Sound();
        menuMusic.setFile(0);
        menuMusic.play();
        menuMusic.loop();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        } else {
            g2.setColor(Color.ORANGE);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
        
        String title = "GunWest";
        Font titleFont = new Font("Georgia", Font.BOLD, 70);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();
        int titleWidth = fmTitle.stringWidth(title);
        int titleHeight = fmTitle.getAscent(); 
        int xTitle = (getWidth() - titleWidth) / 2;
        int yTitle = 120; // the baseline for text
        
        // Shadow for title
        g2.setColor(Color.BLACK);
        g2.drawString(title, xTitle + 3, yTitle + 3);
        // Actual title text
        g2.setColor(Color.WHITE);
        g2.drawString(title, xTitle, yTitle);
        
        Font menuFont = new Font("Courier New", Font.BOLD, 45);
        g2.setFont(menuFont);
        FontMetrics fmMenu = g2.getFontMetrics();
        
        int startY = 260; // where the first menu item is drawn
        int lineSpacing = 60; // vertical spacing between items
        
        for (int i = 0; i < options.length; i++) {
            String item = options[i];
            int itemWidth = fmMenu.stringWidth(item);
            int itemHeight = fmMenu.getAscent();
            int xItem = (getWidth() - itemWidth) / 2;
            int yItem = startY + i * lineSpacing;
            
            // Draw shadow
            g2.setColor(Color.BLACK);
            g2.drawString(item, xItem + 2, yItem + 2);
            
            // If it's selected, use highlight color
            if (i == selectedIndex) {
                g2.setColor(Color.YELLOW);
            } else {
                g2.setColor(Color.WHITE);
            }
            g2.drawString(item, xItem, yItem);
        }
    }

    // KeyListener events to navigate through menu
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W:
                selectedIndex--;
                if (selectedIndex < 0) {
                    selectedIndex = options.length - 1;
                }
                repaint();
                break;

            case KeyEvent.VK_S:
                selectedIndex++;
                if (selectedIndex >= options.length) {
                    selectedIndex = 0;
                }
                repaint();
                break;

            case KeyEvent.VK_SPACE:
                if (selectedIndex == 0) {
                    parentFrame.startHosting();
                    menuMusic.stop();
                } else if (selectedIndex == 1) {
                    parentFrame.startJoining();
                    menuMusic.stop();
                } else if (selectedIndex == 2) {
                    showInstructions(); // Show instructions when selected
                }
                break;
        }
    }


    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    // Method to display the instructions
    private void showInstructions() {
        String instructions = "For the first 3 games, players can boost their speed with stars\n"
                + "Players can use 3 weapons: the sniper, the shotgun & the pistol\n\n"
                + "Pistol -> 0.5 heart | Shotgun -> 1 heart | Sniper -> 4 hearts\n\n"
                + "Players must shoot until a contestant achieves seven kills.";

        JOptionPane.showMessageDialog(this, instructions, "Game Instructions", JOptionPane.INFORMATION_MESSAGE);
    }
}
