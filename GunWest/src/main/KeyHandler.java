package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	private GamePanel gp;
	public boolean upPressed, downPressed, leftPressed, rightPressed;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
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
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
