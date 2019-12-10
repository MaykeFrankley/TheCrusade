package controller;

import java.awt.event.KeyEvent;

public class KeyHandler{

public static final int NUM_KEYS = 14;
	
	public static boolean keyState[] = new boolean[NUM_KEYS];
	public static boolean prevKeyState[] = new boolean[NUM_KEYS];
	
	public static int SPACE = 0;
	public static int A = 1;
	public static int D = 2;
	public static int Q = 3;
	public static int W = 4;
	public static int E = 5;
	public static int R = 6;
	public static int T = 7;
	public static int F = 8;
	public static int ENTER = 9;
	public static int UP = 10;
	public static int DOWN = 11;
	public static int ESCAPE = 12;
	public static int F5 = 13;
	
	public static void keySet(int i, boolean b) {
		//Player1 
		if(i == KeyEvent.VK_SPACE) keyState[SPACE] = b;
		else if(i == KeyEvent.VK_A) keyState[A] = b;
		else if(i == KeyEvent.VK_D) keyState[D] = b;
		else if(i == KeyEvent.VK_Q) keyState[Q] = b;
		else if(i == KeyEvent.VK_W) keyState[W] = b;
		else if(i == KeyEvent.VK_E) keyState[E] = b;
		else if(i == KeyEvent.VK_R) keyState[R] = b;
		else if(i == KeyEvent.VK_T) keyState[T] = b;
		else if(i == KeyEvent.VK_F) keyState[F] = b;
		else if(i == KeyEvent.VK_ENTER) keyState[ENTER] = b;
		else if(i == KeyEvent.VK_UP) keyState[UP] = b;
		else if(i == KeyEvent.VK_DOWN) keyState[DOWN] = b;
		else if(i == KeyEvent.VK_ESCAPE) keyState[ESCAPE] = b;
		else if(i == KeyEvent.VK_F5) keyState[F5] = b;
		
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
