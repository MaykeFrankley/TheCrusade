package model;

public class ScoreInfos {
	
	private String playerName;
	private int scorePoints;
	private long time;
	
	public ScoreInfos(String name, int score, long time){
		super();
		this.playerName = name;
		this.scorePoints = score;
		this.time = time;
	}
	
	public String toString(){
		return playerName+", "+scorePoints+", "+time;
	}

}
