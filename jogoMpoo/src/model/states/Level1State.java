package model.states;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JOptionPane;

import controller.KeyHandler;
import controller.KeyHandler2;
import controller.SalvarGameInfos;
import model.Background;
import model.BossLevel1;
import model.DeadAnimation;
import model.Enemy;
import model.GameInfos;
import model.Ghost;
import model.HellHound;
import model.Item;
import model.NPC;
import model.Player;
import model.Player2;
import model.Rain;
import model.TileMap;
import model.audio.AudioPLayer;
import view.GUI;
import view.HealthBar;
import view.JogoPanel;


public class Level1State extends GameState{
	
	private TileMap tileMap;
	private Background bg, bg2, bg3;
	private static Player player;
	private static Player2 player2;
	
	public static int playerx = 0;
	public static int playery = 0;
	private int[] gameInfos = new int[]{};
	
	private ArrayList<Rain> rain;
	private static boolean raining = false;
	
	private static boolean acertouEnigma;
	public static boolean Bossteleported;
	
	private int revivingPorcentage = 0;
	
	public static int enemyX = 0;
	
	public static int indexEnigma = 0;
	
	public static int trigger = 0;
	public static int trigger2 = 0;
	
	public static final int levelID = 1;
	
	public boolean multiplayer = MenuState.multiplayer;
		
	private Random random = new Random();
	
	private NPC ghost;
	private NPC Fountain;
	
	private  ArrayList<Enemy> enemies;
	private ArrayList<DeadAnimation> deadAnimation;
	private ArrayList<Item> crates, arrowItems, lifeItems, potionItems;
	private Enemy boss;
	
	private GUI gui;
	private boolean bossState;
	
	private static SalvarGameInfos xml;
	
	private static final int PLAYERX = 1;
	private static final int PLAYERY = 2;
	private static final int LIFES = 3;
	private static final int HEALTH = 4;
	private static final int ARROWS = 5;
	private static final int TRIGGER = 6;
	private static final int TRIGGER2 = 7;
	private static final int GAMETIME = 8;
	private static final int SCORE = 9;
	
	
	public Level1State(GameStateManager gsm){	
		super(gsm);
		init();
		
	}

	@Override
	public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/TileSetsAndSprites/tileset1.gif");
		tileMap.loadMap("/Map/Level1Map.csv");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		enemies = new ArrayList<Enemy>();
		crates = new ArrayList<Item>();
		arrowItems = new ArrayList<Item>();
		lifeItems = new ArrayList<Item>();
		potionItems = new ArrayList<Item>();
		
		bg = new Background();
		bg2 = new Background();
		bg3 = new Background();
		
		bg.AddBg("/Backgrounds/Level1BgCasas.png", 1);
		bg2.AddBg("/Backgrounds/Level1BgCasas2.png", 0.5);
		bg3.AddBg("/Backgrounds/Level1BgNuvens.png", 1); 
		
		bg3.setVector(0.2, 0);
		
		player = new Player(tileMap);
		if(MenuState.loadSave){
			xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
			lerGameInfo();
			player.setPosition(gameInfos[PLAYERX], gameInfos[PLAYERY]);
			player.setArrows(gameInfos[ARROWS]-1000);
			player.addHealth(gameInfos[HEALTH]);
			player.setLife(gameInfos[LIFES]);
			player.setScore(gameInfos[SCORE]);
			trigger = gameInfos[TRIGGER];
			trigger2 = gameInfos[TRIGGER2];
			GUI.setGameTime(gameInfos[GAMETIME]);
			xml = null;
			
		}
		else{
			player.setPosition(50, 255);
			trigger = 0;
			trigger2 = 0;
			Salvar();
		}
		
//		player.setPosition(1250, 255);
		
		if(multiplayer){
			player2 = new Player2(tileMap);
			player2.setPosition(player.getx(), 255);
			
		}
		
		AudioPLayer.loop("Level1Bg");

		
		if(trigger2 < 1){
			populateEnemies(150, 250, 20, 55);
			populateItems();
		}
		
		gui = new GUI(player, player2);
		
		deadAnimation = new ArrayList<>();
		
		rain = new ArrayList<>();
		
		rain.add(new Rain(tileMap));
		rain.add(new Rain(tileMap));
		rain.add(new Rain(tileMap));
		
		//NPC
		ghost = new NPC(tileMap, "/TileSetsAndSprites/NpcGhost.png", 4, 37, 65);		
		Fountain = new NPC(tileMap, "/Backgrounds/Fontain.png", 5, 326, 208);
	}
	
	public void lerGameInfo(){
		List<GameInfos> s = xml.lerGameInfos();
		String arr = s.toString().replaceAll("[\\[\\]]", "");;
		String[] integerStrings = arr.split(" "); 
		int[] integers = new int[integerStrings.length]; 
		
		for (int i = 0; i < integers.length; i++){
		    integers[i] = Integer.parseInt(integerStrings[i]); 
		}
		
		gameInfos = integers;
	}
	
	public static void setAcertouEnigma(boolean b){
		acertouEnigma = b;
	}
	
	public static void setTeleported(boolean b){
		Bossteleported = b;
	}
	
	public static void setTrigger(int t){
		trigger = t;
	}
	
	public static Player getPlayer1(){
		return player;
	}
	
	public static Player2 getPlayer2(){
		return player2;
	}
	
	private void populateEnemies(String nome, int x, int y){

		if(nome == "Ghost"){
			Ghost g;
			g = new Ghost(tileMap);
			g.setPosition(x, y);
			g.setName(nome);
			enemies.add(g);
		}
		
		else if(nome == "HellHound"){
			HellHound h;
			h = new HellHound(tileMap);			
			h.setPosition(x, y);
			h.setName(nome);
			enemies.add(h);
		}
		
	}

	private void populateEnemies(int x, int y, int qnt, int dist){		
		
		for (int i = 0; i < qnt; i++) {
			
			Ghost g;
			g = new Ghost(tileMap);			
			g.setPosition(x+i*dist, y);
			g.setName("Ghost");
			
			enemies.add(g);
		}	
		
	}
	
	public boolean getRandomBoolean(float p){
		return random.nextFloat() < p;
	}
	
	public void setDeadAnimation(){
		deadAnimation.add(new DeadAnimation(60, 60, boss.getx(), boss.gety(), 10, "/TileSetsAndSprites/HellBeastDead.png"));
		boss = null;
	}
	
	public static void stopMusic(){
		AudioPLayer.stop("Level1Bg");
		AudioPLayer.stop("BossOST");
		AudioPLayer.stop("Rain");
		raining = false;
	}

	private void populateItems(){
		
		Point[] points2 = new Point[]{
				new Point(380, 256),
				new Point(400, 256),
				new Point(650, 256),
				new Point(700, 256),
				new Point(850, 256),
				new Point(950, 256),
				new Point(1050, 256),
				new Point(1100, 256)
			};
		
		for (int i = 0; i < points2.length; i++) {

			boolean valor1 = getRandomBoolean(0.75f);
			boolean valor2 = getRandomBoolean(0.5f);
			boolean valor3 = getRandomBoolean(0.25f);
			
			Item crate;						
			crate = new Item(tileMap, "/TileSetsAndSprites/Crate.png", 1, 4, true);
			crate.setPosition(points2[i].x, points2[i].y);
			crate.setName("Crate");
			crate.setTemFlecha(valor1);
			crate.setTemPotion(valor2);
			crate.setTemVida(valor3);
			crates.add(crate);
		}
	}
	
	private void populateItems(int x, int y, boolean temVida, boolean temPotion, boolean temFlecha){

		Item crate;

		crate = new Item(tileMap, "/TileSetsAndSprites/Crate.png", 1, 4, true);
		crate.setPosition(x, y);
		crate.setName("Crate");
		crate.setTemFlecha(temFlecha);
		crate.setTemVida(temVida);
		crate.setTemPotion(temPotion);
		crates.add(crate);
	}
	
	
	@Override
	public void update() {
		//Rain
		for (int i = 0; i < rain.size(); i++) {
			Rain r = rain.get(i);
			r.update();
			
			if(i == 0){
				r.setPosition(320, 180);
			}
			else if(i == 1){
				r.setPosition(320+640, 180);
			}
			else{
				r.setPosition(320+640+600, 180);
			}
			
		}
		//
		ghost.update();
		ghost.setPosition(1510, 240);
		
		Fountain.update();
		Fountain.setPosition(1200, 172);
		
		if(multiplayer){
			handleInput2();
			player2.update();
		}
		
		handleInput();
		player.update();
		
		
		tileMap.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
		bg3.update();
		
		player.checkAttack(enemies);
		player.checkItem(crates);
		player.checkItem(lifeItems);
		player.checkItem(potionItems);
		player.checkItem(arrowItems);
		if(boss != null)player.checkAttackBoss(boss);
		
		if(multiplayer){
			player2.checkAttack(enemies);
			player2.checkItem(crates);
			player2.checkItem(lifeItems);
			player2.checkItem(potionItems);
			player2.checkItem(arrowItems);
			if(boss != null){
				player2.checkAttackBoss(boss);
			}
		}
		
		if(tileMap.getx() < 0){
			bg.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			bg2.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			
			if(tileMap.getx() == -890){
				bg.setPosition(-890, 0);
				bg2.setPosition(-890, 0);
			}
		}
		
		//Revive
		if(MenuState.multiplayer){
			if(player2.getx()-player.getx() > 1 && player.gety() == player2.gety() ||
				player2.getx()-player.getx() < 1 && player.gety() == player2.gety()){
				
				if(player.isDead()){
					if(player2.isReviving()){
						revivingPorcentage++;
					}
					
					if(revivingPorcentage >= 500){
						player.setDead(false);
						player.addHealth(500);
					}
				}
				else if(player2.isDead()){
					if(player.isReviving()){
						revivingPorcentage++;
					}
					
					if(revivingPorcentage >= 500){
						player2.setDead(false);
						player2.setHealth(500);
					}
				}
				
				if(revivingPorcentage >= 510){
					revivingPorcentage = 0;
				}
				
			}
		}
		
		if(boss != null){
			boss.update();
		}
		
		
		for(int i = 0; i < enemies.size(); i++){
			Enemy e = enemies.get(i);
			e.update();
			if(e.getx() <= 40){
				e.setPosition(60, 250);
			}
		}
		
		for(int i = 0; i < enemies.size(); i++){
			Enemy e = enemies.get(i);
			if(e.isDead()){
				if(e.getName() == "Ghost"){
					player.increaseScore(20);
					deadAnimation.add(new DeadAnimation(32, 32, e.getx(), e.gety(), 6, "/TileSetsAndSprites/GhostDead.png"));
				}
				else if(e.getName() == "HellHound"){
					player.increaseScore(50);
					deadAnimation.add(new DeadAnimation(44, 52, e.getx(), e.gety(), 5, "/TileSetsAndSprites/FireDeathAnimation.png"));
				}
				enemies.remove(i);
				i--;
				
			}
			
		}
		
		
		for (int i = 0; i < deadAnimation.size(); i++) {
			deadAnimation.get(i).update();
			if(deadAnimation.get(i).shouldRemove()){
				deadAnimation.remove(i);
				i--; 
			}
		}
		
		if(boss != null && boss.isDead()){
			setDeadAnimation();
			player.increaseScore(500);	
		}		

		
		//Update crates
		for (int i = 0; i < crates.size(); i++) {
			Item item = crates.get(i);
			item.update();
		}
		//Update flechas
		for (int i = 0; i < arrowItems.size(); i++) {
			Item item = arrowItems.get(i);
			item.update();
		}
		//Update vidas
		for (int i = 0; i < lifeItems.size(); i++) {
			Item item = lifeItems.get(i);
			item.update();
		}
		//Update potes
		for (int i = 0; i < potionItems.size(); i++) {
			Item item = potionItems.get(i);
			item.update();
		}
		
			
		for (int i = 0; i < crates.size(); i++) {
			Item crate = crates.get(i);
			if(crate.shouldRemove()){
				if(crate.TemFlecha() && !crate.TemVida()&& !crate.tempotion()){
					Item flecha;
					flecha = new Item(tileMap, "/Backgrounds/Arrow.png", 4, 4, false);
					flecha.setPosition(crate.getx(), crate.gety());
					flecha.setName("Arrow");
					arrowItems.add(flecha);
				}
				else if(crate.TemVida() && !crate.TemFlecha()&& !crate.tempotion()){
					Item coracao;
					coracao = new Item(tileMap, "/Backgrounds/Heart.png", 1 , 4, false);
					coracao.setPosition(crate.getx(), crate.gety());
					coracao.setName("Heart");
					lifeItems.add(coracao);
				}
				
				else if(crate.tempotion() && !crate.TemFlecha() && !crate.TemVida()){
					Item potion;
					potion = new Item(tileMap, "/TileSetsAndSprites/HealthPotion.png", 1, 4, false);
					potion.setPosition(crate.getx(), crate.gety());
					potion.setName("Potion");
					potionItems.add(potion);
				}
				AudioPLayer.play("Crate");
				crates.remove(i);
				i--;
			}
		}
		
		
		for (int i = 0; i < arrowItems.size(); i++) {
			Item e = arrowItems.get(i);
	
			if(player.intersects(e)){
				if(player.getArrows() < 1000){
					player.setArrows(100);
					e.setHit();
					arrowItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getArrows() < 1000){
					player2.setArrows(100);
					e.setHit();
					arrowItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}

		}

		for (int i = 0; i < lifeItems.size(); i++) {
			Item e = lifeItems.get(i);
			
			if(player.intersects(e)){
				if(player.getLife() < 3){
					player.setLife(1);
					e.setHit();
					lifeItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getLife() < 3){
					player2.setLife(1);
					e.setHit();
					lifeItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
		}
		
		for (int i = 0; i < potionItems.size(); i++) {
			Item e = potionItems.get(i);
			
			if(player.intersects(e)){
				if(player.getHealth() < 500){
					player.addHealth(150);
					e.setHit();
					potionItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getHealth() < 500){
					player2.addHealth(150);
					e.setHit();
					potionItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}

		}

		if(player.getx() == 0){
			player.setPosition(10, 255);
		}
		
		if(multiplayer){
			if(player2.getx() == 0){
				player2.setPosition(10, 255);
			}
			if(player2.getx() == 1520){
				player2.setPosition(1500, 255);
			}
		}
		
		if(multiplayer){
			if(player2.notOnScreen()){
				player2.setPosition(player.getx()+10, player.gety());
			}
		}

		if(multiplayer){
			if(player.isDead() && player2.isDead()){
				stopMusic();
				MenuState.loadSave = false;
				raining = false;
				gsm.setState(GameStateManager.LEVEL1STATE);
				
			}
		}
		else{
			if(player.isDead()){
				stopMusic();
				MenuState.loadSave = false;
				raining = false;
				gsm.setState(GameStateManager.LEVEL1STATE);			
			}
		}
		
		if(player.getx() >= 1500){
			player.setPosition(1500, 255);
			
			if(enemies.isEmpty() && boss == null || enemies.isEmpty() && boss.isDead()){
				gsm.setEnimga1(true);
				AudioPLayer.stop("Level1Bg");
				if(acertouEnigma){
					gsm.setEnimga1(false);
					player.setPosition(1450, 249);
					player.setRight(false);
					stopMusic();
					Salvar();
					gsm.setState(GameStateManager.LEVEL2STATE);
				}
			}
			else{
				player.setPosition(1450, 249);
				player.setRight(false);
			}	
		}
		
		if(player.getx() >= 1400 && trigger == 0 && !bossState){ // Trigger boss
			setBossState();
			trigger = 5;
		}
	
		
		if(trigger == 1){
			gsm.setEnimga1(false);
			setBossState();
			player.setPosition(1400, 249);
			player.setRight(false);
			AudioPLayer.loop("BossOST");
			AudioPLayer.loop("Rain");
			trigger = 0;
			
		}
		
		
		//boss Teleport
		
		if(BossLevel1.fireCounts == 1 && trigger == 0){
			int randomNum = ThreadLocalRandom.current().nextInt(300, 1400 + 1);
			boss.setPosition(randomNum, 255);
			Bossteleported = true;
			trigger = 2;
		}
		
		//Uso Externo
		for (int i = 0; i < enemies.size(); i++) {
			enemyX = enemies.get(i).getx();
		}
		
		playerx = player.getx();
		playery = player.gety();
		
		if(enemies.isEmpty() && boss == null ||enemies.isEmpty() && boss.isDead()){
			PauseState.setSalvavel(true);
			trigger2 = 1;
		}else{
			PauseState.setSalvavel(false);
			trigger2 = 0;
			
		}
		
//		System.out.println(revivingPorcentage);
//		System.out.println("Player1x = "+player.getx());
//		System.out.println("Player2x = "+player2.getx());
//		System.out.println(trigger);

	}
	
	public void setBossState(){
		AudioPLayer.stop("Level1Bg");
		populateEnemies(150, 250, 10, 60);
		int i = 0;
		int pos = 0;
		while(i < 5){
			populateEnemies("HellHound", 200+pos, 255);
			i++;
			pos += 100;
		}
		populateItems(200, 255, true, false, false);
		populateItems(450, 255, false, true, false);
		populateItems(640, 255, false, true, false);
		populateItems(1200, 255, false, true, false);
		populateItems(500, 255, false, false, true);
		populateItems(700, 255, false, false, true);
		populateItems(750, 255, false, false, true);
		populateItems(1300, 255, false, false, true);
		populateItems(560, 255, false, false, true);
		indexEnigma ++;
		if(indexEnigma > 3) indexEnigma = 0;
		raining = true;
		bg.AddBg("/Backgrounds/Level1BgCasasB.png", 1);
		bg2.AddBg("/Backgrounds/Level1BgCasas2B.png", 0.5);
		bg3.AddBg("/Backgrounds/Level1BgNuvensB.png", 0.2);
		boss = new BossLevel1(tileMap);
		boss.setName("HellBeast");
		boss.setPosition(1200, 255);
		AudioPLayer.loop("BossOST");
		AudioPLayer.loop("Rain");
		trigger = 0;
		
		bossState = true;
	}

	@Override
	public void draw(Graphics2D g) {	
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, JogoPanel.LARGURA, JogoPanel.ALTURA);
		
		bg3.draw(g);
		bg2.draw(g);
		bg.draw(g);
		
		Fountain.draw(g);
		//Items Draw
		if(arrowItems.size() > 0){
			for (int j = 0; j < arrowItems.size(); j++) {
				arrowItems.get(j).draw(g);
			}
		}
		if(lifeItems.size() > 0){
			for (int j = 0; j < lifeItems.size(); j++) {
				lifeItems.get(j).draw(g);
			}
		}
		if(potionItems.size() > 0){
			for (int j = 0; j < potionItems.size(); j++) {
				potionItems.get(j).draw(g);
			}
		}
			
		for (int i = 0; i < crates.size(); i++) {
			crates.get(i).draw(g);
		}
		
		tileMap.draw(g);
		player.draw(g);
		
		if(multiplayer){
			player2.draw(g);
			HealthBar player2Barra = new HealthBar(tileMap, "Player2", player2.getHealth()/20);
			player2Barra.setPosition(player2.getx()-10, player2.gety());
			player2Barra.draw(g);
		}
		
		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		
		if(boss != null){
			boss.draw(g);
		}
		
		if(enemies.isEmpty()){
			ghost.draw(g);
		}
		

		for (int i = 0; i < deadAnimation.size(); i++) {
			deadAnimation.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
			deadAnimation.get(i).draw(g);
		}
		
		//Revive
		if(MenuState.multiplayer){		
			if(player.isDead()){
				g.setColor(Color.RED);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.drawString(String.valueOf(revivingPorcentage/5+"%"), JogoPanel.LARGURA/2, 340);
			}
			else if(player2.isDead()){
				g.setColor(Color.GREEN);
				g.setFont(new Font("Arial", Font.BOLD, 10));
				g.drawString(String.valueOf(revivingPorcentage/5+"%"), JogoPanel.LARGURA/2, 340);
			}
		}
		
		if(raining){
			for (int i = 0; i < rain.size(); i++) {
				rain.get(i).draw(g);
			}
		}

		gui.draw(g);
		
		
	}
	
	public static void Salvar(){
		xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
		xml.salvarGameInfos(new GameInfos(levelID, player.getx(), player.gety(), player.getLife(), player.getHealth(), player.getArrows(), trigger, trigger2, GUI.Time, Player.getScore()));	
		xml = null;
	}

	public void handleInput() {
		//Player1
		if(KeyHandler.isPressed(KeyHandler.ESCAPE)){
			gsm.setPaused(true);
		}
		if(!player.isInvunerable()){
			player.setleft(KeyHandler.keyState[KeyHandler.A]);
			player.setRight(KeyHandler.keyState[KeyHandler.D]);
			player.setReviving(KeyHandler.keyState[KeyHandler.ENTER]);
			player.setJumping(KeyHandler.keyState[KeyHandler.SPACE]);
			if(KeyHandler.isPressed(KeyHandler.Q)) player.setAttacking1(true);
			if(KeyHandler.isPressed(KeyHandler.W)) player.setAttacking2(true);
			if(KeyHandler.isPressed(KeyHandler.E)) player.setSlide(true);
			if(KeyHandler.isPressed(KeyHandler.F)) player.setAirAttack();
			if(KeyHandler.isPressed(KeyHandler.R)) player.setMagicing(true);
			if(KeyHandler.isPressed(KeyHandler.T)) player.setUsingBow(true);		
		}

	}
	
	public void handleInput2(){
		//Player2
		if(!player2.isInvunerable()){
			player2.setleft(KeyHandler2.keyState[KeyHandler2.LEFT]);
			player2.setRight(KeyHandler2.keyState[KeyHandler2.RIGHT]);
			player2.setReviving(KeyHandler.keyState[KeyHandler.ENTER]);
			player2.setJumping(KeyHandler2.keyState[KeyHandler2.UP]);
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD1)) player2.setAttacking1(true);
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD2)) player2.setAttacking2(true);
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD3)) player2.setSlide(true);
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD6)) player2.setAirAttack();
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD4)) player2.setMagicing(true);
			if(KeyHandler2.isPressed(KeyHandler2.NUMPAD5)) player2.setUsingBow(true);
		
		}
	}
	
	

}
