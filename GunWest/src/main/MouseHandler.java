package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseMotionListener {
    private int mouseX, mouseY;
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e); 
    }
    public int getMouseX() { return mouseX; }
    public int getMouseY() { return mouseY; }
}
