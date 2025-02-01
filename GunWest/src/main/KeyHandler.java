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
			System.out.println("W key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			this.downPressed = true;
			System.out.println("S key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			this.leftPressed = true;
			System.out.println("A key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			this.rightPressed = true;
			System.out.println("D key was pressed");
		}
	}
	
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			this.upPressed = false;
			System.out.println("W key was released");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			this.downPressed = false;
			System.out.println("S key was released");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			this.leftPressed = false;
			System.out.println("A key was released");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			this.rightPressed = false;
			System.out.println("D key was released");
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
