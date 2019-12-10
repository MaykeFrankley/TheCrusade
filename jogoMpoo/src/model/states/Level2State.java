package model.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import controller.KeyHandler;
import controller.KeyHandler2;
import controller.SalvarGameInfos;
import model.Background;
import model.BossLevel2;
import model.DeadAnimation;
import model.Enemy;
import model.FireTrap;
import model.GameInfos;
import model.Ghost;
import model.HellBeast;
import model.HellHound;
import model.Item;
import model.MapObject;
import model.NPC;
import model.Player;
import model.Player2;
import model.Skeleton;
import model.TileMap;
import model.audio.AudioPLayer;
import view.GUI;
import view.HealthBar;
import view.JogoPanel;
import view.SetPlayerName;

public class Level2State extends GameState{

	private int[] gameInfos = new int[]{};

	public static final int level2ID = 2;

	public boolean multiplayer = MenuState.multiplayer;

	private static SalvarGameInfos xml;

	private static final int LEVELID = 0;
	private static final int PLAYERX = 1;
	private static final int PLAYERY = 2;
	private static final int LIFES = 3;
	private static final int HEALTH = 4;
	private static final int ARROWS = 5;
	private static final int TRIGGER = 6;
	private static final int TRIGGER2 = 7;
	private static final int GAMETIME = 8;
	private static final int SCORE = 9;

	//Inicia em 0
	public static int trigger = 0;
	public static int trigger2 = 0;


	private TileMap tileMap;
	private Background bg, bg2, bg3, bg4, bg5, bg6, bg7, bg8, bg9;
	public static Player player;
	public static Player2 player2;

	private Random random = new Random();

	private static ArrayList<Enemy> enemies;
	private ArrayList<DeadAnimation> deadAnimation;
	private ArrayList<Item> crates, arrowItems, lifeItems, potionItems;
	private static Enemy boss;

	private NPC enigmaBg;
	private NPC fountain;
	private GUI gui;

	private int revivingPorcentage = 0;

	public static boolean acertouEnigma;

	public Level2State(GameStateManager gsm){
		super(gsm);
		init();
	}

	@Override
	public void init() {	
		tileMap = new TileMap(30);
		tileMap.loadTiles("/TileSetsAndSprites/tileset2.png");
		tileMap.loadMap("/Map/Level2Map.csv");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);

		enemies = new ArrayList<Enemy>();

		//		gsm.setEnigma2(true);

		crates = new ArrayList<Item>();
		arrowItems = new ArrayList<Item>();
		lifeItems = new ArrayList<Item>();
		potionItems = new ArrayList<Item>();

		bg = new Background();
		bg2 = new Background();
		bg3 = new Background();
		bg4 = new Background();
		bg5 = new Background();
		bg6 = new Background();
		bg7 = new Background();
		bg8 = new Background();
		bg9 = new Background();

		bg.AddBg("/Backgrounds/Level2/Level2Nuvens.png", 1.5);
		bg2.AddBg("/Backgrounds/Level2/Level2Camada1.png", 0.5);
		bg3.AddBg("/Backgrounds/Level2/Level2Camada2.png", 0.4);
		bg4.AddBg("/Backgrounds/Level2/Level2Camada3.png", 1);
		bg5.AddBg("/Backgrounds/Level2/Level2Luzes1.png", 1.5);
		bg6.AddBg("/Backgrounds/Level2/DungeonBg2.png", 1);
		bg7.AddBg("/Backgrounds/Level2/BlackScreen.png", 1);
		bg8.AddBg("/Backgrounds/Level2/DungeonBg.png", 1);
		bg9.AddBg("/Backgrounds/Level2/Level2Casas.png", 1);

		AudioPLayer.loop("Level2Bg");

		enigmaBg = new NPC(tileMap, "/Backgrounds/Level2/NpcEnigma.png", 1, 128, 64);
		fountain = new NPC(tileMap, "/Backgrounds/Fontain.png", 5, 326, 208);

		bg5.setVector(0.5, 0);
		bg.setVector(0.5, 0);

		xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
		lerGameInfo();

		player = new Player(tileMap);

		if(MenuState.loadSave){
			if(gameInfos[LEVELID] == 1){
				player.setHealth(gameInfos[HEALTH]);
				player.setArrows(gameInfos[ARROWS]-1000);
				player.setLife(gameInfos[LIFES]);
				trigger = 0;
				trigger2 = 0;
				GUI.setGameTime(gameInfos[GAMETIME]);
				player.setPosition(40, 255);
				Salvar();
			}
			else{
				player.setPosition(gameInfos[PLAYERX], gameInfos[PLAYERY]);
				player.setArrows(gameInfos[ARROWS]-1000);
				player.addHealth(gameInfos[HEALTH]);
				player.setLife(gameInfos[LIFES]);
				player.setScore(gameInfos[SCORE]);
				trigger = gameInfos[TRIGGER];
				trigger2 = gameInfos[TRIGGER2];
				GUI.setGameTime(gameInfos[GAMETIME]);
			}
		}
		else{
			trigger = 0;
			trigger2 = 0;
			player.setPosition(40, 255);
		}

		//		player.setPosition(1450, 915);
		//		player.setPosition(655, 655);
		//		player.setPosition(860, 1425);

		if(MenuState.multiplayer){
			player2 = new Player2(tileMap);
			player2.setPosition(player.getx()+40, player.getx());
		}
		gui = new GUI(player, player2);

		populateEnemies();
		populateItems();

		deadAnimation = new ArrayList<>();

	}

	public static void stopAllMusic(){
		AudioPLayer.stop("Fire");
		AudioPLayer.stop("Level2Bg");
		AudioPLayer.stop("BossOST");
	}

	public static boolean setAllDead(){
		for (int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.setDead(true);
		}
		if(boss != null){
			boss.setDead(true);
		}

		return true;

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

	private void populateEnemies(){

		//HELLHOUND
		Point[] points1 = new Point[]{
				new Point(200, 255),
				new Point(400, 255),
				new Point(600, 255),
				new Point(800, 255),
				new Point(850, 255),
				new Point(950, 255),
				new Point(700, 255),
				new Point(1050, 255),
				new Point(1200, 255),
				new Point(1300, 255),
				new Point(1350, 255),

		};


		for (int i = 0; i < points1.length; i++) {

			HellHound h;
			h = new HellHound(tileMap);			
			h.setPosition(points1[i].x, points1[i].y);
			h.setName("HellHound");
			enemies.add(h);
		}


		//FIRETRAP
		Point[] points2 = new Point[]{
				new Point(1080, 445),
				new Point(980, 445),
				new Point(880, 445),
				new Point(780, 445),
				new Point(680, 445),
				new Point(580, 445),
				new Point(480, 445),
				new Point(420, 445)

		};


		for (int i = 0; i < points2.length; i++) {

			FireTrap f;
			f = new FireTrap(tileMap);			
			f.setPosition(points2[i].x, points2[i].y);
			f.setName("FireTrap");
			enemies.add(f);
		}

		//GHOSTS
		Point[] points3 = new Point[]{
				new Point(500, 550),
				new Point(300, 550),
				new Point(400, 550),
				new Point(200, 550),
				new Point(500, 790),
				new Point(300, 790),
				new Point(400, 790),
				new Point(200, 790),

		};

		for (int i = 0; i < points3.length; i++) {

			Ghost g;
			g = new Ghost(tileMap);
			g.setPosition(points3[i].x, points3[i].y);
			g.setName("Ghost");
			enemies.add(g);
		}

	}

	private void populateEnemies(String nome, int x, int y){

		if(nome == "Skeleton"){
			Skeleton s;
			s = new Skeleton(tileMap);			
			s.setPosition(x, y);
			s.setName(nome);
			enemies.add(s);
		}

		else if(nome == "Ghost"){
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

		else if(nome == "FireTrap"){
			FireTrap f;
			f = new FireTrap(tileMap);			
			f.setPosition(x, y);
			f.setName(nome);
			enemies.add(f);
		}

		else if(nome == "HellBeast"){
			HellBeast h;
			h = new HellBeast(tileMap);
			h.setPosition(x, y);
			h.setName(nome);
			enemies.add(h);
		}
	}

	public boolean getRandomBoolean(float p){
		return random.nextFloat() < p;
	}

	private void populateItems(){

		Point[] points2 = new Point[]{
				new Point(600, 255),
				new Point(650, 255),
				new Point(750, 255),
				new Point(850, 255),
				new Point(950, 255),
				new Point(1000, 255),
				new Point(1050, 255),
				new Point(1100, 255),
				new Point(150, 795),
				new Point(220, 795),
				new Point(260, 795),
				new Point(900, 855),
				new Point(980, 855),
				new Point(1200, 855),
				new Point(1450, 1035),
				new Point(1420, 1035),
				new Point(40, 1425),
				new Point(100, 1425),
				new Point(120, 1425),
				new Point(140, 1425),
				new Point(160, 1425)
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
			crate.setTemVida(valor3);
			crate.setTemPotion(valor2);
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

	public void update() {
		if(!MenuState.loadSave){
			Salvar();
			MenuState.loadSave = true;
		}
		handleInput();
		player.update();

		if(MenuState.multiplayer){
			handleInput2();
			player2.update();
		}

		tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), 0);

		bg5.update();
		bg.update();
		enigmaBg.update();
		enigmaBg.setPosition(570, 1410);

		fountain.update();

		if(player.gety() >= 1275){
			fountain.setPosition(1340, 1334);
		}else{
			fountain.setPosition(1230, 315);
		}
		player.checkAttack(enemies);
		if(boss != null)player.checkAttackBoss(boss);
		if(boss != null & MenuState.multiplayer)player2.checkAttackBoss(boss);
		player.checkItem(crates);
		player.checkItem(lifeItems);
		player.checkItem(potionItems);
		player.checkItem(arrowItems);

		if(MenuState.multiplayer){
			player2.checkAttack(enemies);
			player2.checkItem(crates);
			player2.checkItem(lifeItems);
			player2.checkItem(potionItems);
			player2.checkItem(arrowItems);
		}

		if(tileMap.getx() < 0){
			bg2.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			bg3.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			bg4.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			bg8.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 0);
			bg9.setPosition(JogoPanel.LARGURA / 2 - player.getx()+880, 38);

			if(tileMap.getx() == -890){
				bg2.setPosition(-890, 0);
				bg3.setPosition(-890, 0);
				bg4.setPosition(-890, 0);
				bg8.setPosition(-890, 0);
				bg9.setPosition(0, 38);
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

				if(revivingPorcentage >= 500){
					revivingPorcentage = 0;
				}

			}
		}

		for(int i = 0; i < enemies.size(); i++){
			Enemy e = enemies.get(i);
			e.update();
			if(e.getx() <= 40){
				e.setPosition(60, e.gety());
			}
		}

		if(boss != null){
			boss.update();

			if(boss.intersects(player)){
				player.hit(100);
			}
			if(MenuState.multiplayer && boss.intersects(player2)){
				player2.hit(100);
			}

			if(boss.isDead() && !player.isDead()){
				player.increaseScore(500);
				acertouEnigma = false;
				stopAllMusic();
				setAllDead();
				if(enemies.isEmpty()){
					new SetPlayerName();
					gsm.setState(GameStateManager.SCORE);
					boss = null;
				}
			}
		}

		for(int i = 0; i < enemies.size(); i++){
			Enemy e = enemies.get(i);
			if(e.isDead()){
				if(e.getName() == "HellHound"){
					player.increaseScore(50);
					deadAnimation.add(new DeadAnimation(44, 52, e.getx(), e.gety(), 5, "/TileSetsAndSprites/FireDeathAnimation.png"));
				}

				else if(e.isDead() && e.getName()=="Ghost"){
					player.increaseScore(20);
					deadAnimation.add(new DeadAnimation(32, 32, e.getx(), e.gety(), 6, "/TileSetsAndSprites/GhostDead.png"));
				}

				else if(e.getName() == "Skeleton"){
					player.increaseScore(10);
					deadAnimation.add(new DeadAnimation(44, 52, e.getx(), e.gety(), 5, "/TileSetsAndSprites/FireDeathAnimation.png"));
				}

				else if(e.getName() == "HellBeast"){
					player.increaseScore(100);
					deadAnimation.add(new DeadAnimation(60, 60, e.getx(), e.gety(), 10, "/TileSetsAndSprites/HellBeastDead.png"));
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
					e.setHit();
					player.setArrows(100);
					AudioPLayer.play("Item");
					arrowItems.remove(i);
					i--;

				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getArrows() < 1000){	
					e.setHit();
					player2.setArrows(100);		
					AudioPLayer.play("Item");
					arrowItems.remove(i);
					i--;
				}			
			}
		}

		for (int i = 0; i < lifeItems.size(); i++) {
			Item e = lifeItems.get(i);

			if(player.intersects(e)){
				if(player.getLife() < 3){
					e.setHit();
					player.setLife(1);
					lifeItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getLife() < 3){
					e.setHit();
					player2.setLife(1);
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
					e.setHit();
					player.addHealth(150);
					potionItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}
			}
			else if(multiplayer && player2.intersects(e)){
				if(player2.getHealth() < 500){
					e.setHit();
					player2.addHealth(150);
					potionItems.remove(i);
					i--;
					AudioPLayer.play("Item");
				}			
			}

		}

		for (int i = 0; i < enemies.size(); i++) {
			if(enemies.get(i).getName() == "HellHound"){
				HellHound h = (HellHound) enemies.get(i);
				if(enemies.get(i).getx() - player.getx()  == 200 || enemies.get(i).getx() - player.getx()  == -200){
					h.attack();
				}
			}
		}

		if(player.getx() <= 0 && player.gety() <= 255){
			player.setPosition(10, 255);
		}

		if(MenuState.multiplayer){
			if(player2.getx() == 0 && player2.gety() == 215){
				player2.setPosition(10, 255);
			}
			if(player2.notOnScreen()){
				player2.setPosition(player.getx()+10, player.gety());
			}
		}

		if(player.gety() >= 315){
			if(tileMap.getx() < 0){
				bg6.setPosition(JogoPanel.LARGURA / 2 - player.getx(), 20);
				if(tileMap.getx() == -890){
					bg6.setPosition(-890, 20);
				}
			}
		}
		if(player.gety() >= 580){
			if(tileMap.getx() < 0){
				bg6.setPosition(JogoPanel.LARGURA / 2 - player.getx(), -105);
				if(tileMap.getx() == -890){
					bg6.setPosition(-890, -105);
				}
			}
		}

		if(player.gety() >= 800){
			if(tileMap.getx() < 0){
				bg6.setPosition(JogoPanel.LARGURA / 2 - player.getx(), -135);
				if(tileMap.getx() == -890){
					bg6.setPosition(-890, -135);
				}
			}
		}

		if(player.gety() >= 914){
			if(tileMap.getx() < 0){
				bg6.setPosition(JogoPanel.LARGURA / 2 - player.getx(), -255);
				if(tileMap.getx() == -890){
					bg6.setPosition(-890, -255);
				}
			}
		}

		if(player.gety() > 1215){
			if(tileMap.getx() < 0){
				bg8.setPosition(JogoPanel.LARGURA / 2 - player.getx(), -780);
				if(tileMap.getx() == -890){
					bg8.setPosition(-890, -780);
				}
			}
		}


		if(MenuState.multiplayer){
			if(player.isDead() && player2.isDead()){
				MenuState.loadSave = true;
				acertouEnigma = false;
				if(boss != null){
					boss.setDead(true);
				}
				stopAllMusic();
				if(enemies.isEmpty() && boss == null){
					trigger = gameInfos[TRIGGER];
					trigger2 = gameInfos[TRIGGER2];
					gsm.setState(GameStateManager.MENUSTATE);
				}

			}
		}
		else{
			if(player.isDead()){
				MenuState.loadSave = true;
				acertouEnigma = false;
				setAllDead();
				if(boss != null){
					boss.setDead(true);
				}
				stopAllMusic();
				if(enemies.isEmpty() && boss == null){
					trigger = gameInfos[TRIGGER];
					trigger2 = gameInfos[TRIGGER2];
					gsm.setState(GameStateManager.MENUSTATE);
				}
			}
		}


		//Gatilhos

		if(player.getx() >= 1440 && player.gety() <= 345){
			if(trigger == 0){
				//				sfxFire.loop();
				AudioPLayer.loop("Fire");
				trigger = 1;
			}
		}

		if(player.gety() == 675){
			//			sfxFire.stop();
			AudioPLayer.stop("Fire");
		}

		if(player.getx() >= 450 && player.gety() == 555){
			if(trigger == 1){
				populateItems(50, 675, true, false, false);
				populateItems(80, 675, false, false, true);
				populateItems(110, 675, false, false, true);
				populateItems(140, 675, false, false, true);
				trigger = 2;
			}

		}


		if(player.getx() <= 380 && player.gety() == 675){
			if(trigger == 2){
				traps("Skeleton", 575, 675, 8, 2000);
				populateEnemies("HellHound", 200, 855);
				populateEnemies("HellHound", 400, 855);
				populateEnemies("HellHound", 600, 855);
				populateEnemies("HellHound", 800, 855);
				trigger = 3;				
			}
		}


		if(player.getx() >= 1150 && player.gety() == 855){
			if(trigger <= 3){
				//				sfxFire.loop();
				AudioPLayer.loop("Fire");
				traps("FireTrap", 1400, 855, 13, 1500);
				trigger = 4;
			}	
		}


		if(player.getx() >= 990 && player.gety() == 675){
			if(trigger == 4){
				//				sfxFire.stop();
				AudioPLayer.loop("Fire");
				traps("Skeleton", 1200, 675, 6, 2000);
				trigger = 5;
			}
		}

		if(player.getx() <= 1450 && player.gety() == 915){
			if(enemies.size() > 0){
				for (int i = 0; i < enemies.size(); i++) {
					enemies.get(i).setDead(true);
				}
			}
			trigger = 6;
		}

		if(player.getx() >= 1420 && player.gety() == 1035){
			if(trigger == 6){
				populateEnemies("HellBeast", 1035, 1035);
				trigger = 7;
			}
		}

		if(player.getx() >= 980 && player.gety() == 1095){
			if(trigger <= 7){
				populateEnemies("HellBeast", 1380, 1095);
				populateEnemies("HellBeast", 1080, 1155);
				populateEnemies("HellBeast", 1450, 1215);
				populateEnemies("HellBeast", 1275, 1275);
				trigger = 8;
			}
		}

		if(player.getx() <= 950 && player.gety() == 1425){
			if(trigger == 8){
				//				bgMusic.stop();
				AudioPLayer.stop("Level2Bg");
				trigger = 9;
			}	
		}

		if(player.gety() == 1425){
			AudioPLayer.stop("Level2Bg");
		}

		if(player.getx() <= 570 && player.gety() == 1425 && trigger == 9){
			gsm.setEnigma2(true);
			player.setPosition(600, 1425);
			Salvar();
			trigger = 10;
			PauseState.salvou = true;
		}


		if(trigger == 10 && acertouEnigma){
			//			bgMusicBoss.loop();
			AudioPLayer.loop("BossOST");
			traps("Ghost", 1490, 1410, 10, 1000);
			traps("Skeleton", 800, 1425, 4, 3000);
			trigger = 11;
		}

		if(trigger == 11 && acertouEnigma){
			boss = new BossLevel2(tileMap);
			boss.setPosition(200, 1200);
			trigger = 12;
		}

		if(BossLevel2.trigger2 == 3){
			for(int i = 0; i < 4; i++){
				populateItems(400+i*15, 1425, false, false, true);
			}
			traps("Ghost", 1490, 1410, 6, 1000);

			BossLevel2.trigger2 = 4;			
		}

		if(player.getx() <= 205 && player.gety() == 405){ // Limpar hellhounds
			for (int i = 0; i < enemies.size(); i++) {
				Enemy e = enemies.get(i);
				if(e.getName() == "HellHound"){
					enemies.remove(i);
					i--;
				}
			}
		}

		if(player.getx() >= 1431 && player.gety() == 345 || player.getx() >= 1350 && player.gety() == 375 || player.getx() >= 1130 && player.gety() == 405 ||
				player.getx() <= 210 && player.gety() == 405 || trigger == 3 && player.getx() <= 670 && player.gety() == 675|| player.getx() <= 1400 && player.gety() == 915){

			//			System.out.println("SALVAVEL");
			PauseState.setSalvavel(true);
		}else{
			//			System.out.println("NÃO SALVAVEL");
			PauseState.setSalvavel(false);;
		}


	}

	private void traps(String enemy, int posx, int posy, int qnt, int miliSec){

		class criarTraps extends TimerTask{
			int cancel = 0;
			int pos = posx;
			public void run() {
				populateEnemies(enemy, pos, posy);
				pos -= 100;
				if(pos < 40){
					pos = 200;
				}    
				cancel++;
				if(cancel == qnt){
					cancel();	   
				}
			}
		}

		Timer timer = new Timer();
		timer.schedule(new criarTraps(), 0, miliSec);	
	}


	@Override
	public void draw(Graphics2D g) {
		bg7.draw(g);

		//System.out.println("Trigger == "+trigger);
//		System.out.println("Player1x = "+player.getx());
//		System.out.println("Player1y = "+player.gety());
		//System.out.println("Player2x = "+player2.getx());

		if(player.gety() < 315){
			bg.draw(g);
			bg4.draw(g);
			bg3.draw(g);
			bg2.draw(g);
			bg5.draw(g);
			bg9.draw(g);
			bg9.setPosition(880, 38);
		}

		else if(player.gety() >= 315){
			tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), -270);
			bg6.draw(g);
			if(player.gety() >= 300 && player.gety() <= 405 || player.gety() <= 555){
				fountain.draw(g);
			}

			if(player.gety() > 580){
				tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), -490);
				if(player.gety() > 800){
					tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), -520);
					if(player.gety() > 914){
						tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), -890);
						if(player.gety() > 1215){
							tileMap.setPosition(JogoPanel.LARGURA / 2- player.getx(), -1110);
							bg8.draw(g);
							fountain.draw(g);
						}
					}
				}
			}
		}

		for (int i = 0; i < crates.size(); i++) {
			crates.get(i).draw(g);
		}

		tileMap.draw(g);

		enigmaBg.draw(g);

		if(boss != null){
			boss.draw(g);
		}

		player.draw(g);
		//Barra de vida
		HealthBar playerBarra = new HealthBar(tileMap, "Player", player.getHealth()/20);
		playerBarra.setPosition(player.getx()-10, player.gety());
		playerBarra.draw(g);

		if(MenuState.multiplayer){
			player2.draw(g);
			HealthBar player2Barra = new HealthBar(tileMap, "Player2", player2.getHealth()/20);
			player2Barra.setPosition(player2.getx()-10, player2.gety());
			player2Barra.draw(g);
		}

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



		for (int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}


		for (int i = 0; i < deadAnimation.size(); i++) {
			deadAnimation.get(i).setMapPosition((int)tileMap.getx(), (int)tileMap.gety());
			deadAnimation.get(i).draw(g);
		}


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

		gui.draw(g);

	}

	public static void Salvar(){
		xml = new SalvarGameInfos("GameInfos", "GameInfos.xml");
		xml.salvarGameInfos(new GameInfos(level2ID, player.getx(), player.gety(), player.getLife(), player.getHealth(), player.getArrows(), trigger, trigger2, GUI.Time, Player.getScore()));
		xml = null;
	}

	public void handleInput(){
		//Player1
		if(KeyHandler.isPressed(KeyHandler.ESCAPE))gsm.setPaused(true);

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
