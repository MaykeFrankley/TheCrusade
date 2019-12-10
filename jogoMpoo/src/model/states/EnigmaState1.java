package model.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import controller.KeyHandler;
import model.Background;

public class EnigmaState1 extends GameState {
	
	private Background bg;
	private int currentChoice = 0;
	
	private String[] enigma1 = {"Essa coisa te devora.Pássaros, monstros, arvores e flores.","Mastiga o ferro e morde o aço.","Reduz em pó as rochas mais resistentes","Mata o rei e destrói as cidades"};
	private String[] enigma2 = {"É maior que Deus, e mais maléfico que o Diabo.", "Os pobres têm-no e os ricos precisam dele.", " Se o comermos, morremos.","De que estou a falar?"};
	private String[] enigma3 = {"Sempre quando chego trago a tristeza."," Mas sempre chego na hora certa."," As vezes venho depois de uma doença."," E outras, depois de um ato de violência"};
	private String[] enigma4 = {"Logo desde o seu nascer.","Come bem e muito corre.","Mas se lhe dão de beber, Logo num instante morre."," Quem sou eu?"};
	
	private String[][] enigmas = {enigma1, enigma2, enigma3, enigma4};
	
	private String[] opcoes = {"A morte", "O tempo", "O fogo", "O Nada"};
	
	private Font font, font2;
	
	public EnigmaState1(GameStateManager gsm) {
		super(gsm);
		try {

			bg = new Background();
			bg.AddBg("/Backgrounds/EnigmaBg.png", 1);
			bg.setPosition(180, 100);
			
			font2 = new Font("Arial", Font.PLAIN, 9);
			font = new Font("Century Gothic", Font.PLAIN, 9);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void init() {}
	
	public void update() {
		bg.update();
		
		handleInput();
	}
	
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.setFont(font);
		
		for (int i = 0; i < enigmas.length; i++) {
			g.setColor(Color.WHITE);
			g.drawString(enigmas[Level1State.indexEnigma][i], 210, 120+i*20);

		}
		
		for (int i = 0; i < opcoes.length; i++) {
			g.setFont(font2);
			if(i == currentChoice){
				g.setColor(Color.GREEN);
				
			}else{
				g.setColor(Color.WHITE);
			}
			
			g.drawString(opcoes[i], 310, 195+i*15);
			
		}
	}
	
	private void select(){
		if(currentChoice == 0){
			if(Level1State.indexEnigma == 2){
				Level1State.setAcertouEnigma(true);
			}
			else{
				Level1State.setAcertouEnigma(false);
				Level1State.setTrigger(1);
			}
		}
		
		if(currentChoice == 1){
			if(Level1State.indexEnigma == 0){
				Level1State.setAcertouEnigma(true);
			}
			else{
				Level1State.setAcertouEnigma(false);
				Level1State.setTrigger(1);
			}
			
		}
		
		if(currentChoice == 2){
			if(Level1State.indexEnigma == 3){
				Level1State.setAcertouEnigma(true);
			}
			else{
				Level1State.setAcertouEnigma(false);
				Level1State.setTrigger(1);
			}
		}
		
		if(currentChoice == 3){
			if(Level1State.indexEnigma == 1){
				Level1State.setAcertouEnigma(true);
			}
			else{
				Level1State.setAcertouEnigma(false);
				Level1State.setTrigger(1);
			}
		}
		
		gsm.setEnimga1(false);
	}
			

	
	public void handleInput() {
		if(KeyHandler.isPressed(KeyHandler.UP)){
			currentChoice--;
			if(currentChoice == -1){
				currentChoice = opcoes.length - 1;
			}
		}
		if(KeyHandler.isPressed(KeyHandler.DOWN)){
			currentChoice++;
			if(currentChoice == opcoes.length){
				currentChoice = 0;
			}
		}
		
		if(KeyHandler.isPressed(KeyHandler.ENTER)){
			select();
		}
	}

	@Override
	public void handleInput2() {
		// TODO Auto-generated method stub
		
	}

}
