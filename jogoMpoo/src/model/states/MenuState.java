package model.states;

import java.awt.*;
import java.awt.Graphics2D;
import java.util.List;

import controller.KeyHandler;
import controller.SalvarGameInfos;
import model.audio.AudioPLayer;
import view.JogoPanel;
import model.Background;
import model.GameInfos;

public class MenuState extends GameState{
	
	private Background bg;
	private int currentChoice = 0;
	
	private String[] opcoes = {"SinglePlayer", "Multiplayer", "Créditos", "Help", "Score", "Sair"};
	private String[] opcoes2;
	private String[] confirm = {"Sim", "Não"};
	private boolean aviso;
	
	private Color titleColor;
	private Font titleFont;
	private Font font;

	private boolean chooseSave;
	private boolean menuPrincipal = true;
	
	public static boolean multiplayer;
	public static boolean loadSave;
	
	private SalvarGameInfos xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
	
	public MenuState(GameStateManager gsm){
		super(gsm);
		
		try {

			bg = new Background();
			bg.AddBg("/Backgrounds/menuBg.png", 1);
			bg.setVector(-0.4, 0);
			
			titleColor = new Color(255,255,255);
			titleFont = new Font("Century Gothic", Font.BOLD, 28);
			font = new Font("Arial", Font.PLAIN, 12);
			
			AudioPLayer.load("/Music/Menu.mp3", "MenuBg");
			AudioPLayer.loop("MenuBg");
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		if(lerLevelID() == 0){
			opcoes2 = new String[]{"Novo Jogo", "Voltar"};
		}else{
			opcoes2 = new String[]{"Continuar", "Novo Jogo", "Voltar"};
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

	@Override
	public void init(){}

	@Override
	public void update() {
		bg.update();
		handleInput();
		
	}

	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, JogoPanel.LARGURA, JogoPanel.ALTURA);
		bg.draw(g);
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("The Crusade", 80, 70);
		
		g.setFont(font);
		
		if(!chooseSave && menuPrincipal){
			for (int i = 0; i < opcoes.length; i++) {
				if(i == currentChoice){
					g.setColor(Color.YELLOW);

				}else{
					g.setColor(Color.WHITE);
				}

				g.drawString(opcoes[i], 145, 140+i*15);

			}	
		}
		else{
			if(!aviso){
				for (int i = 0; i < opcoes2.length; i++) {
					if(i == currentChoice){
						g.setColor(Color.YELLOW);
	
					}else{
						g.setColor(Color.WHITE);
					}
					
					if(multiplayer){
						g.drawString("Multiplayer", 250, 120);
					}
					g.drawString(opcoes2[i], 145, 140+i*15);
				}
			}
			else{
				for (int i = 0; i < confirm.length; i++) {
					if(i == currentChoice){
						g.setColor(Color.YELLOW);
	
					}else{
						g.setColor(Color.WHITE);
						g.drawString("Já existe um jogo salvo, deseja continuar?", 200, 100);
					}
	
					g.drawString(confirm[i], 145, 140+i*15);
				}
				
			}
		}
	}
	
	

	
	private void select(){
		if(menuPrincipal){
			AudioPLayer.loop("MenuBg");
			if(currentChoice == 0){
				chooseSave = true;
				menuPrincipal = false;
				AudioPLayer.stop("MenuBg");
				
			}
			if(currentChoice == 1){
				multiplayer = true;
				chooseSave = true;
				menuPrincipal = false;
				AudioPLayer.stop("MenuBg");;
				
			}
			if(currentChoice == 2){
				gsm.setState(GameStateManager.CREDIT);
				AudioPLayer.stop("MenuBg");
			}
			
			if(currentChoice == 3){
				gsm.setState(GameStateManager.HELP);
				AudioPLayer.stop("MenuBg");
				
			}
			if(currentChoice == 4){
				gsm.setState(GameStateManager.SCORE);
				AudioPLayer.stop("MenuBg");
			}
			if(currentChoice == 5){
				System.exit(0);
			}
		}
		
		else if(chooseSave){
			//ChooseSave
			if(lerLevelID() > 0){
				
				if(currentChoice == 0){ //Continuar
					loadSave = true;
					if(lerLevelID() == 1){
						AudioPLayer.stop("MenuBg");
						gsm.setState(GameStateManager.LEVEL1STATE);	
					}
					else if(lerLevelID() == 2){
						AudioPLayer.stop("MenuBg");
						gsm.setState(GameStateManager.LEVEL2STATE);
						
					}	
				}
				
				if(currentChoice == 1){ // Novo Jogo
					aviso = true;
					chooseSave = false;
						
				}
				
				if(currentChoice == 2){ // Voltar
					menuPrincipal = true;
					chooseSave = false;
					multiplayer = false;
					AudioPLayer.loop("MenuBg");
				}
				
			}
			else{
				
				if(currentChoice == 0){ // NovoJogo
					loadSave = false;
					AudioPLayer.stop("MenuBg");
					gsm.setState(GameStateManager.LEVEL1STATE);
				}
				else if(currentChoice == 1){ // voltar
					menuPrincipal = true;
					chooseSave = false;
					AudioPLayer.loop("MenuBg");
				}		
			}	
		}
		else{
			if(currentChoice == 0){ // Confirma Sim
				loadSave = false;
				menuPrincipal = true;
				AudioPLayer.stop("MenuBg");
				gsm.setState(GameStateManager.LEVEL1STATE);
			}
			else if(currentChoice == 1){ // Confirma Nao
				chooseSave = true;
				aviso = false;
			}
			
		}
		
		currentChoice = 0;
	}
	
	public void handleInput(){
		if(KeyHandler.isPressed(KeyHandler.UP)){
			currentChoice--;
			if(currentChoice == -1){
				if(menuPrincipal)currentChoice = opcoes.length - 1;
				if(chooseSave)currentChoice = opcoes2.length - 1;
				if(aviso)currentChoice = confirm.length - 1;
			}
		}
		if(KeyHandler.isPressed(KeyHandler.DOWN)){
			currentChoice++;
			if(menuPrincipal){
				if(currentChoice == opcoes.length){
					currentChoice = 0;
				}
			}
			
			if(chooseSave){
				if(currentChoice== opcoes2.length){
					currentChoice = 0;
				}
			}
			
			if(aviso){
				if(currentChoice == confirm.length){
					currentChoice = 0;
				}
			}
			
		}
		
		if(KeyHandler.isPressed(KeyHandler.ENTER)){
			select();
		}
	}
	
	public void handleInput2(){}

}
