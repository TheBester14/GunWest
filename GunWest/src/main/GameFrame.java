package main;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
        
        // Ensure the game panel gets focus so key events work
        panel.requestFocusInWindow();
        
        // When the window gains focus, ask the panel to request focus.
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                panel.requestFocusInWindow();
            }
        });
    }
    
    public GamePanel getGamePanel() {
        return panel;
    }
}
