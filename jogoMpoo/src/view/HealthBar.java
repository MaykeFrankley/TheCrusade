package view;

import java.awt.Color;
import java.awt.Graphics2D;

import model.MapObject;
import model.TileMap;
import model.states.MenuState;

public class HealthBar extends MapObject{
	
	private int enemyHealth;
	private int playerHealth;
	private int player2Health;
	private int bossHealth;
	
	private String objectName;

	public HealthBar(TileMap tm, String objectName, int health) {
		super(tm);
		
		
		if(objectName == "Player"){
			this.playerHealth = health;
		}
		
		else if(objectName == "Enemy"){
			this.enemyHealth = health;
		}
		
		else if(objectName == "Player2"){
			this.player2Health = health;
		}
		else if(objectName == "Boss"){
			this.bossHealth = health;
		}
		
		this.objectName = objectName;
	}
	
	public void update(){
		setPosition(xtemp, ytemp);
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		g.setColor(Color.RED);
		
		if(objectName == "Enemy"){
			if(MenuState.multiplayer){
				g.fillRect((int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2), enemyHealth/4, 3);
			}else{
				g.fillRect((int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2), enemyHealth/2, 3);
			}
		}
		else if(objectName == "Player"){
			g.fillRect((int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2)-20, playerHealth, 3);
		}
		
		else if(objectName == "Player2"){
			g.setColor(Color.GREEN);
			g.fillRect((int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2)-20, player2Health, 3);
		}
		
		else if(objectName == "Boss"){
			g.setColor(Color.BLUE);
			g.fillRect((int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2), bossHealth/10, 10);
		}
		
	}

}
