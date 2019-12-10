package model.states;

import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import model.audio.AudioPLayer;
import view.JogoPanel;

public class GameStateManager {
	
	private GameState[] gameStates;
	private int currentState;
	
	private PauseState pauseState;
	private boolean paused;

	private EnigmaState1 enigma1;
	private boolean enigma1Ativado;
	
	private EnigmaState2 enigma2;
	private boolean enigma2Ativado;
	
	public static final int NUMGAMESTATES = 6;
	public static final int MENUSTATE = 0;
	public static final int LEVEL1STATE = 1;
	public static final int LEVEL2STATE = 2;
	public static final int HELP = 3;
	public static final int CREDIT = 4;
	public static final int SCORE = 5;
	
	
	public GameStateManager(){
		
		AudioPLayer.init();
		
		gameStates = new GameState[NUMGAMESTATES];
		
		pauseState = new PauseState(this);
		paused = false;
		
		enigma1 = new EnigmaState1(this);
		enigma1Ativado = false;
		
		enigma2 = new EnigmaState2(this);
		enigma2Ativado = false;
				
		currentState = MENUSTATE;
		loadState(currentState);
			
	}
	
	private void loadState(int state){
		if(state == MENUSTATE){
			gameStates[state] = new MenuState(this);
		}
		if(state == LEVEL1STATE){
			gameStates[state] = new Level1State(this);
		}
		
		if(state == LEVEL2STATE){
			gameStates[state] = new Level2State(this);
		}
		if(state == HELP){
			gameStates[state] = new Help(this);
		}
		
		if(state == CREDIT){
			gameStates[state] = new Creditos(this);
		}
		if(state == SCORE){
			gameStates[state] = new ScoreMenuState(this);
		}
	}
	
	private void unloadState(int state){
		gameStates[state] = null;
	}

	public void setState(int state){
		unloadState(currentState);
		currentState = state;
		loadState(currentState);
	}
	
	public void setPaused(boolean b) {
		paused = b;
	}
	
	public void setEnimga1(boolean b) {
		enigma1Ativado = b;
	}
	
	public void setEnigma2(boolean b){
		enigma2Ativado = b;
	}
	
	public int getCurrentState(){
		return currentState;
	}
	
	public void update(){
		if(paused) {
			pauseState.update();
			return;
		}
		
		if(enigma1Ativado){
			enigma1.update();
			return;
		}
		
		if(enigma2Ativado){
			enigma2.update();
			return;
		}
		
		if(gameStates[currentState] != null) gameStates[currentState].update();
	}
	
	public void draw(Graphics2D g){
		if(paused) {
			pauseState.draw(g);
			return;
		}
		
		if(enigma1Ativado){
			enigma1.draw(g);
			return;
		}
		
		if(enigma2Ativado){
			enigma2.draw(g);
			return;
		}
		
		if(gameStates[currentState] != null) gameStates[currentState].draw(g);
		else {
			g.setColor(java.awt.Color.BLACK);
			g.fillRect(0, 0, JogoPanel.LARGURA, JogoPanel.ALTURA);
		}
	}
	

}
