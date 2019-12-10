package model.states;

import java.awt.*;

import controller.KeyHandler;
import model.Background;

public class Help extends GameState{
	
	private Background bg;
	
	private String[] texto = {"Em The Crusade existe um limite de FireBalls para ser disparado.",
			"Sendo necessário aguardar um pouco para disparar novas FireBalls.",
			"Também existe um limite pro dodge/Escorregada do jogador",
			"O player1 se movimenta nas teclas A e D e pula no Espaco", "O player2 se movimenta e pula nas setas de direção.",
			"Player morto nao poderá se mover, mas poderá atacar. Segure Enter para reviver",
			"Não poderá salvar o jogo com inimigos próximos.",
			"No modo Multiplayer os inimigos tem vida dobrada!."
			};
	
	private Color titleColor;
	private Font titleFont;
	private Font font;
	
	public Help(GameStateManager gsm){
		super(gsm);
		
		try {

			bg = new Background();
			bg.AddBg("/Backgrounds/menuBg.png", 1);
			bg.setVector(0, 0);
			
			titleColor = new Color(255,255,255);
			titleFont = new Font("Century Gothic", Font.PLAIN, 28);
			font = new Font("Arial", Font.PLAIN, 10);
			
			
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
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("The Crusade", 80, 70);
		
		g.setFont(font);

		for (int i = 0; i < texto.length; i++) {
			g.drawString(texto[i], 135, 140+i*20);

		}
	}

	public void handleInput(){
		if(KeyHandler.isPressed(KeyHandler.ENTER)){
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}
	
	public void handleInput2(){}

}
