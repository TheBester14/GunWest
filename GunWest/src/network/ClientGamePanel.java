package network;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.util.Map;

public class ClientGamePanel extends JPanel {
    private Map<Integer, PlayerState> playersMap;

    public ClientGamePanel(Map<Integer, PlayerState> playersMap) {
        this.playersMap = playersMap;
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Fill background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw each player as a circle (red for “Me”, blue for others)
        synchronized(playersMap) {
            for (PlayerState ps : playersMap.values()) {
                if (ps.name.equals("Me")) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLUE);
                }
                int radius = 20;
                int drawX = ps.x - radius/2;
                int drawY = ps.y - radius/2;
                g.fillOval(drawX, drawY, radius, radius);
                g.setColor(Color.BLACK);
                g.drawString("ID: " + ps.id, ps.x - radius/2, ps.y - radius);
            }
        }
    }
}
