package model;


public class GameInfos {
	//Player
	private int playerX;
	private int playerY;
	private int lifes;
	private int health;
	private int arrows;
	//
	
	private int levelID;
	private int trigger;
	private int trigger2;
	
	private long gameTime;
	private int score;
	
	public GameInfos(int levelID, int playerX, int playerY, int lifes, int health, int arrows, int trigger, int trigger2, long gametime, int score) {
		super();
		this.levelID = levelID;
		this.playerX = playerX;
		this.playerY = playerY;
		this.lifes = lifes;
		this.health = health;
		this.arrows = arrows;
		this.trigger = trigger;
		this.trigger2 = trigger2;
		this.gameTime = gametime;
		this.score = score;
	}
	
	
//	public GameInfos(int gametime, int score, String playername){
//		this.gameTime = gametime;
//		this.score = score;
//		this.playerName = playername;
//	}
	
	public String toString() {
		return levelID+" "+playerX+" "+playerY+" "+lifes+" "+health+" "+arrows+" "+trigger+" "+trigger2+" "+gameTime+" "+score;
	}
	
	

}
