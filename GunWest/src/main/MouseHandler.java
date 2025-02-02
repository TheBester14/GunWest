package main;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseHandler implements MouseMotionListener, MouseListener {
    private int mouseX, mouseY;
    private boolean leftDown;
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            leftDown = true;
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            leftDown = false;
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { }
    
    @Override
    public void mouseEntered(MouseEvent e) { }
    
    @Override
    public void mouseExited(MouseEvent e) { }
    
    public int getMouseX() { 
        return mouseX; 
    }
    
    public int getMouseY() { 
        return mouseY; 
    }
    
    public boolean isLeftDown() {
        return leftDown;
    }
}
