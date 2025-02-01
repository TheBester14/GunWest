package main;

import javax.swing.*;

public class GameFrame extends JFrame {
    private GamePanel panel;

    public GameFrame() {
        // Create a new GamePanel instance
        panel = new GamePanel();
        
        // Add the panel to the frame
        this.add(panel);
        
        // Frame settings
        this.setTitle("GunWest");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack(); 
        this.setLocationRelativeTo(null); // Centers the window on the screen
        this.setVisible(true);
    }
}
