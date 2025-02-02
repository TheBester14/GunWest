package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	public boolean oneKey, twoKey, threeKey;
	
	public KeyHandler() {
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			this.upPressed = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			this.downPressed = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			this.leftPressed = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			this.rightPressed = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			this.spacePressed = true;
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			this.upPressed = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			this.downPressed = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			this.leftPressed = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			this.rightPressed = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			this.spacePressed = false;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_1) {
			this.oneKey = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_2) {
			this.twoKey = true;
		}
		
		if (e.getKeyCode() == KeyEvent.VK_3) {
			this.threeKey = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
