package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener{
	GamePanel gp;
	
	public KeyHandler(GamePanel gp) {
		this.gp = gp;
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			System.out.println("W key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_S) {
			System.out.println("S key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_A) {
			System.out.println("A key was pressed");
		}
		
		if (e.getKeyCode() == KeyEvent.VK_D) {
			System.out.println("D key was pressed");
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
