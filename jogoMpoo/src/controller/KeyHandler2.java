package controller;

import java.awt.event.KeyEvent;

public class KeyHandler2 {

public static final int NUM_KEYS = 10;
	
	public static boolean keyState[] = new boolean[NUM_KEYS];
	public static boolean prevKeyState[] = new boolean[NUM_KEYS];
	
	public static int UP = 0;
	public static int RIGHT = 1;
	public static int LEFT = 2;
	public static int NUMPAD1 = 3;
	public static int NUMPAD2 = 4;
	public static int NUMPAD3 = 5;
	public static int NUMPAD4 = 6;
	public static int NUMPAD5 = 7;
	public static int NUMPAD6 = 8;
	public static int NUMPAD8 = 9;
	
	public static void keySet(int i, boolean b) {	
		//Player2
		if(i == KeyEvent.VK_UP) keyState[UP] = b;
		else if(i == KeyEvent.VK_RIGHT) keyState[RIGHT] = b;
		else if(i == KeyEvent.VK_LEFT) keyState[LEFT] = b;
		else if(i == KeyEvent.VK_NUMPAD1) keyState[NUMPAD1] = b;
		else if(i == KeyEvent.VK_NUMPAD2) keyState[NUMPAD2] = b;
		else if(i == KeyEvent.VK_NUMPAD3) keyState[NUMPAD3] = b;
		else if(i == KeyEvent.VK_NUMPAD4) keyState[NUMPAD4] = b;
		else if(i == KeyEvent.VK_NUMPAD5) keyState[NUMPAD5] = b;
		else if(i == KeyEvent.VK_NUMPAD6) keyState[NUMPAD6] = b;
		else if(i == KeyEvent.VK_NUMPAD6) keyState[NUMPAD8] = b;
		
	}
	
	
	public static void update() {
		for(int i = 0; i < NUM_KEYS; i++) {
			prevKeyState[i] = keyState[i];
		}
	}
	
	public static boolean isPressed(int i) {
		return keyState[i] && !prevKeyState[i];
	}
	
	public static boolean anyKeyPress() {
		for(int i = 0; i < NUM_KEYS; i++) {
			if(keyState[i]) return true;
		}
		return false;
	}

}
