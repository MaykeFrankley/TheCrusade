package model.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JOptionPane;

import controller.KeyHandler;
import controller.SalvarGameInfos;
import model.BossLevel2;
import model.GameInfos;
import model.audio.AudioPLayer;
import view.JogoPanel;


public class PauseState extends GameState {
	
	
	private Font font;
	private Color color;
	private int currentChoice = 0;
	
	public static boolean salvavel;
	public static boolean salvou;
	private boolean menuPrincipal = true;
	private SalvarGameInfos xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
	
	private boolean aviso;
	private String[] confirm = {"Sim", "Não"};
	private String[] opcoes;
	
	public PauseState(GameStateManager gsm) {
		
		super(gsm);
		font = new Font("Century Gothic", Font.BOLD, 14);
		color = new Color(0, 0, 0, 0.1f);
		
		
	}
	
	private void checkOpcoes(){
		if(salvavel){
			opcoes = new String[]{"Resume", "Salvar Jogo", "Voltar ao Menu"};
		}
		else{
			opcoes = new String[]{"Resume", "Voltar ao Menu"};
		}
	}
	
	private int lerLevelID(){
		List<GameInfos> s = xml.lerGameInfos();
		
		if(s != null){
			String arr = s.toString().replaceAll("[\\[\\]]", "");;
			String[] integerStrings = arr.split(" "); 
			int[] integers = new int[integerStrings.length]; 
			
			for (int i = 0; i < integers.length; i++){
			    integers[i] = Integer.parseInt(integerStrings[i]); 
			}
	
			return integers[0];
		}
		else{
			return 0;
		}
	
	}
	
	public static void setSalvavel(boolean b){
		salvavel = b;
	}
	
	public void init() {}
	
	public void update() {
		handleInput();
	}
	
	public void draw(Graphics2D g) {
		
		g.setColor(color);
		g.setFont(font);
		g.fillRect(0, 0, JogoPanel.LARGURA, JogoPanel.ALTURA);
		checkOpcoes();
		if(!aviso){
			for (int i = 0; i < opcoes.length; i++) {
				if(i == currentChoice){
					g.setColor(Color.YELLOW);
	
				}else{
					g.setColor(Color.WHITE);
				}
	
				g.drawString(opcoes[i], 200, 140+i*15);
	
			}
		}
		else{
			if(currentChoice == -1){
				currentChoice = 0;
			}
			for (int i = 0; i < confirm.length; i++) {
				if(i == currentChoice){
					g.setColor(Color.YELLOW);

				}else{
					g.setColor(Color.WHITE);
					g.drawString("Sair sem salvar?", 200, 100);
				}
				
				g.drawString(confirm[i], 200, 160+i*15);

			}
		}
				
	}
	
	private void select(){
		
		if(menuPrincipal){
			if(salvavel){
				if(currentChoice == 0){ //Resume
					gsm.setPaused(false);
				}
				
				if(currentChoice == 1){ //Salvar
					if(lerLevelID() == 1 || lerLevelID() == 0){
						Level1State.Salvar();
						JOptionPane.showMessageDialog(null, "Jogo Salvo");
						salvou = true;
					}
					else if(lerLevelID() == 2){
						Level2State.Salvar();
						JOptionPane.showMessageDialog(null, "Jogo Salvo");
						salvou = true;
					}
				}
				
				if(currentChoice == 2){ // Voltar ao menu
					if(salvou){
						salvou = false;
						gsm.setPaused(false);
						
						if(gsm.getCurrentState() == 1){
							Level1State.stopMusic();
							gsm.setState(GameStateManager.MENUSTATE);
						}
						else if(gsm.getCurrentState() == 2){
							Level2State.stopAllMusic();
							BossLevel2.trigger2 = 0;
							if(Level2State.setAllDead())gsm.setState(GameStateManager.MENUSTATE);
						}
						
						
							
					}
					else{
						aviso = true;
						menuPrincipal = false;
					}	
				}
			}
			else{
				if(currentChoice == 0){ //Resume
					gsm.setPaused(false);
				}
				
				if(currentChoice == 1){ // Voltar ao menu
					if(salvou){
						if(gsm.getCurrentState() == 1){
							Level1State.stopMusic();
						}
						else if(gsm.getCurrentState() == 2){
							Level2State.stopAllMusic();
							AudioPLayer.stop("BossWings");
							AudioPLayer.stop("Skeleton");
						}
						gsm.setPaused(false);
						gsm.setState(GameStateManager.MENUSTATE);
						
					}
					else{
						aviso = true;
						menuPrincipal = false;
					}	
				}
			}
		}
		else{ // Confirmação

			if(currentChoice == 0){ //Sim
				aviso = false;
				menuPrincipal = true;
				gsm.setPaused(false);
				if(gsm.getCurrentState() == 1){
					Level1State.stopMusic();
					gsm.setState(GameStateManager.MENUSTATE);
				}
				else if(gsm.getCurrentState() == 2){
					Level2State.stopAllMusic();
					Level2State.setAllDead();
					gsm.setState(GameStateManager.MENUSTATE);
				}
				
			}
			if(currentChoice == 1){//Nao
				aviso = false;	
				menuPrincipal = true;
			}
		}
		
		currentChoice = 0;
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
	public void handleInput2() {}

}
