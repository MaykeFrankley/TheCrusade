package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.Level1State;
import model.states.MenuState;

public class BossLevel1 extends Enemy{
	
	private int fireDamage;
	private int fire;
	private int maxFire;
	private int fireCost;
	private ArrayList<FireBall> fireBalls;
	
	public static int fireCounts = 0;
	
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {6, 2, 4, 4};
	
	private static final int IDLE = 0;
	private static final int APPEAR1 = 1;
	private static final int APPEAR2 = 2;
	private static final int ATTACK = 3;

	public BossLevel1(TileMap tm) {
		super(tm);
		
		moveSpeed = 0;
		maxSpeed = 0;
		stopSpeed = 0;
		fallSpeed = 0;
		maxFallSpeed = 0;
		jumpStart = 0;
		stopJumpSpeed = 0;
		
		width = 60;
		height = 60;
		cwidth = 30;
		cheight = 30;
		
		if(MenuState.multiplayer){
			health = maxHealth = 3000;
		}else{
			health = maxHealth = 1500;
		}
		damage = 100;
		
		fire = maxFire = 200;
		fireDamage = 100;
		fireCost = 200;
		fireBalls = new ArrayList<FireBall>();
		
		AudioPLayer.load("/SFX/HellBeast.mp3", "HellBeast");
		
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/HellBeastSprite.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 4; i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++){
					bi[j] = spritesheet.getSubimage(j*width, i*height, width, height);
				}

				sprites.add(bi);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = APPEAR1;
		animation.setFrames(sprites.get(APPEAR1));
		animation.setDelay(250);
		
		right = true;
		facingRight = true;
		
	}
	
	public void update(){
		
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(right && x > Level1State.playerx){
			right = false;
			left = true;
			facingRight = false;

		}
		else if(left && x < Level1State.playerx){
			right = true;
			left = false;
			facingRight = true;

		}
					
		if(currentAction == APPEAR1){
			if(animation.hasPLayedOnce()){
				currentAction = APPEAR2;
				animation.setFrames(sprites.get(APPEAR2));
				animation.setDelay(200);
			}
		}
		
		if(currentAction == APPEAR2){
			if(animation.hasPLayedOnce()){
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(200);
			}
		}
		
		if(currentAction == ATTACK){
			if(animation.hasPLayedOnce()){
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(200);
			}
		}
		
		fire += 1;
		if(fire > maxFire) {
			fire = maxFire;
		}
		
		if(currentAction == IDLE){
			if(fire == fireCost){
				fire -= fireCost;
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(200);
				AudioPLayer.play("HellBeast");
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
				fireCounts++;
			}					
		}
		
		if(fireCounts > 1){
			Level1State.setTrigger(0);
			fireCounts = 0;
		}
		
		
		
		if(Level1State.Bossteleported){
			currentAction = APPEAR1;
			animation.setFrames(sprites.get(APPEAR1));
			animation.setDelay(250);
			Level1State.setTeleported(false);
		}
			
		
		animation.update();
		
		
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).update();
			
			if(fireBalls.get(i).intersects(Level1State.getPlayer1())){
				Level1State.getPlayer1().hit(fireDamage);
			}
			
			if(MenuState.multiplayer){
				if(fireBalls.get(i).intersects(Level1State.getPlayer2())){
					Level1State.getPlayer2().hit(fireDamage);
				}
			}

			if(fireBalls.get(i).shouldRemove()){
				fireBalls.remove(i);
				i--;
			}
		}	
	}
	
	public void draw(Graphics2D g){
		
		setMapPosition();
		
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 10));
		g.drawString("HellBeast", 100, 60);
		
		if(MenuState.multiplayer){
			g.setColor(Color.RED);
			g.fillRect(160, 54, getHealth()/15, 10);
		}else{
			g.setColor(Color.RED);
			g.fillRect(160, 54, (int) (getHealth()/7.5), 10);
		}
		
			
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).draw(g);
		}
		
		if(facingRight){
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height/1.5),-width,height,null);
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2), (int)(y+ymap - height/1.5), null);
		}
		
	}
	
	public void setDead(){
		dead = true;
	}

}
