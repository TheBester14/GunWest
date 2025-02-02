package main;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;

public class RemotePlayer {
    private int x, y;
    private int width = 50, height = 50;
    private BufferedImage image;
    
    public RemotePlayer(int x, int y) {
        this.x = x;
        this.y = y;
        // Load the same sprite image (change the path if needed)
        try {
            image = ImageIO.read(getClass().getResource("/character/Walking1.png"));
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void draw(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(image, x, y, width, height, null);
    }
}
