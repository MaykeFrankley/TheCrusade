package model.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import controller.KeyHandler;
import model.Background;

public class Creditos extends GameState{
	
	private Background bg;
	
	private String[] texto = {"Universidade Federal Rural de Pernambuco",
							"UAST",
							"Disciplina: Modelagem e Programação Orientada a Objetos (MPOO)",
							"Docente: Richarlyson D'Emery.",
							"Discente: Mayke Frankley",
							"Data: 04/02/2019 - Serra Talhada."
			
	};
	
	private Color fontColor;
	private Font font;


	public Creditos(GameStateManager gsm) {
		super(gsm);
		
		try {

			bg = new Background();
			bg.AddBg("/Backgrounds/menuBg.png", 1);
			bg.setVector(0, 0);
			
			fontColor = new Color(255,255,0);
			font = new Font("Arial", Font.PLAIN, 12);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update() {
		bg.update();
		handleInput();
		
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.setColor(fontColor);
		
		g.setFont(font);

		for (int i = 0; i < texto.length; i++) {
			g.drawString(texto[i], 145, 140+i*20);

		}
		
	}

	@Override
	public void handleInput() {
		if(KeyHandler.isPressed(KeyHandler.ENTER)){
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
	}

	@Override
	public void handleInput2() {
		// TODO Auto-generated method stub
		
	}
	

}
