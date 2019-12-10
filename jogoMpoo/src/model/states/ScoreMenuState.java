package model.states;

import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import controller.KeyHandler;
import controller.SalvarScoreInfos;
import model.Background;
import model.Player;
import model.ScoreInfos;
import view.GUI;
import view.SetPlayerName;

public class ScoreMenuState extends GameState{
	
	private Background bg;
	private Font fontLabels, fontScores;
	private String[] labels = {"Nome", "Score", "Tempo"};
	private String[] scores;
	private static SalvarScoreInfos xml;
	private static ArrayList<String> myList;
	private int min;
	private int sec;
	
	public static boolean update;

	public ScoreMenuState(GameStateManager gsm) {
		super(gsm);
		
		try {
			bg = new Background();
			bg.AddBg("/Backgrounds/ScoreMenuBg.png", 1);
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		fontLabels = new Font("Century Gothic", Font.BOLD, 14);
		fontScores = new Font("Century Gothic", Font.PLAIN, 12);
		
		scores = readScores();
		if(scores != null){
			myList = new ArrayList<String>(Arrays.asList(scores));
			getFormattedTime();
		}
		
		update = true;

	}
	
	public void updateScores(boolean sorted){
		while(!sorted){
			System.out.println("INLOOP");
			for(int i = 1; i < myList.size(); i += 3){
				if(i+3 < myList.size()){
					if(Integer.parseInt(myList.get(i)) <= Integer.parseInt(myList.get(i+3))){						
						Collections.swap(myList, i-1, i+2);
//						System.out.println("Trocou "+myList.get(i+2)+" Por "+myList.get(i-1));
						Collections.swap(myList, i, i+3);
//						System.out.println("Trocou "+myList.get(i+3)+" Por "+myList.get(i));
						Collections.swap(myList, i+1, i+4);
//						System.out.println("Trocou "+myList.get(i+4)+" Por "+myList.get(i+1));
					}
				}
			}

			CHECK:
				for (int i = 1; i < myList.size(); i += 3) {
					if(i+3 < myList.size()){
//						System.out.println(Integer.parseInt(myList.get(i))+" < "+Integer.parseInt(myList.get(i+3)));
						if (Integer.parseInt(myList.get(i)) < Integer.parseInt(myList.get(i+3))) {
							sorted = false;
							break CHECK;
						}
					}
					sorted = true;
				}

		}

	}
	
	public static void Salvar(){
		xml = new SalvarScoreInfos("ScoreInfos", "ScoreInfos.xml");
		xml.salvarScore(new ScoreInfos(SetPlayerName.txtNome.getText(), Player.getScore(), GUI.Time));
		xml = null;

	}
	
	private String[] readScores(){
		xml = new SalvarScoreInfos("ScoreInfos", "ScoreInfos.xml");
		List<ScoreInfos> s = xml.lerScores();
		if(s != null){
			String arr = s.toString().replaceAll("[\\[\\]]", "");;
			String[] Strings = arr.split(", "); 
			xml = null;
			return Strings;
		}
		else{
			xml = null;
			return null;
		}
	
	}
	
	public void getFormattedTime(){
		
		for (int i = 2; i < myList.size(); i += 3) {
			
			min = (int) (Long.parseLong(myList.get(i)) / 3600);
			sec = (int) ((Long.parseLong(myList.get(i)) % 3600) / 60);
			
			myList.set(i, (sec < 10 ? min + ":0" + sec : min + ":" + sec));
		}
		
	}

	@Override
	public void init() {}

	@Override
	public void update() {
		handleInput();	
		
		if(update){
			scores = readScores();
			if(scores != null){
				myList = new ArrayList<String>(Arrays.asList(scores));
				getFormattedTime();
				updateScores(false);
			update = false;	
			}	
		}
		
	}
	

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		g.setFont(fontLabels);
		for (int i = 0; i < labels.length; i++) {
			g.drawString(labels[i], 185+i*100, 20);
		}
		
		if(scores != null){
			for (int i = 0; i < myList.size(); i += 3) {
				g.setFont(fontScores);
				g.drawString(myList.get(i), 185, 40+i*5);
				
			}
			
			for (int i = 1, j = 0; i < myList.size(); i += 3, j += 3){
				g.setFont(fontScores);
				g.drawString(myList.get(i), 285, 40+j*5);
				
			}
			
			for (int i = 2, j = 0; i < myList.size(); i += 3, j += 3){
				g.setFont(fontScores);
				g.drawString(myList.get(i), 385, 40+j*5);

			}
		}
		
		
	}

	@Override
	public void handleInput() {
		if(KeyHandler.isPressed(KeyHandler.ESCAPE)){
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
	}

	@Override
	public void handleInput2() {}

}
