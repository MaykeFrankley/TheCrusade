package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.MenuState;

public class BossLevel2 extends Enemy{

	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {6, 6, 5};
	
	private static final int IDLE = 0;
	private static final int ATTACKLOAD = 1;
	private static final int ATTACK = 2;
	
	//Trigger inicia em 1
	public static int trigger = 1;
	public static int trigger2 = 0;
	
	public boolean flinching;
	public long flinchTimer;

	public BossLevel2(TileMap tm) {
		super(tm);
		
		moveSpeed = 0.6;
		maxSpeed = 0.9;
		stopSpeed = 0.001;
		fallSpeed = 0;
		maxFallSpeed = 0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		width = 240;
		height = 192;
		cwidth = 80;
		cheight = 60;
		
		if(MenuState.multiplayer){
			health = maxHealth = 4000;
		}else{
			health = maxHealth = 2000;
		}
		
		damage = 200;

		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/BossLevel2.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 3; i++){
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
		currentAction = IDLE;
		animation.setFrames(sprites.get(IDLE));
		animation.setDelay(100);
		facingRight = true;
		right = true;
		
		AudioPLayer.load("/SFX/BossWings.mp3", "BossWings");
		AudioPLayer.load("/SFX/BossAttack.mp3", "BossAttack");
		
		AudioPLayer.loop("BossWings");
		
	}
	
	public void hit(int damage){
		AudioPLayer.play("HitSound");
		if(this.dead) return;
		this.health -= damage;
		if(this.health < 0) this.health = 0;
		if(this.health == 0) this.dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	private void getNextPosition(){
		
		if(left){
			dx -= moveSpeed;
			if(dx < -maxSpeed){
				dx = -maxSpeed;
			}
		}
		else if(right){
			dx += moveSpeed;
			if(dx > maxSpeed){
				dx = maxSpeed;
			}
		}
		else{
			if(dx > 0){
				dx -= stopSpeed;
				if(dx < 0){
					dx = 0;
				}
			}
			else if(dx < 0){
				dx += stopSpeed;
				if(dx > 0){
					dx = 0;
				}
			}
		}
		
		if(jumping && !falling){
			dy = jumpStart;
			falling = true;
		}
		
		if(falling){
			dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			if(dy > maxFallSpeed) dy = maxFallSpeed;
					
		}
		
	}
	
	public void update(){
		
//		System.out.println("BossTrigger = "+trigger);
//		System.out.println("BossTriger2 = "+trigger2);
		
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(right && x > 800){
			right = false;
			left = true;
			facingRight = false;
			trigger++;

		}
		else if(left && x < 200){
			right = true;
			left = false;
			facingRight = true;
			trigger++;
		}
		
		if(y <= 1300){
			setJumping(false);
		}
		
		if((int)x >= 350){
			if(trigger == 3 && trigger2 < 4){
				trigger = 0;
				trigger2++;
				fallSpeed = 0.01;
				maxFallSpeed = 1;
				maxSpeed = 1.6;
				if(ytemp == 1410){
					maxSpeed = 2.5;
					currentAction = ATTACKLOAD;
					if(currentAction == ATTACKLOAD){
						animation.setFrames(sprites.get(ATTACKLOAD));
						animation.setDelay(80);
					}
				}

			}
		}
		
		if(trigger2 == 4){
			setJumping(true);
			fallSpeed = 0;
			maxFallSpeed = 0;
			maxSpeed = 0.9;
			trigger2 = 0;
			trigger = 0;
		}
		
		if(currentAction == ATTACKLOAD){
			if(animation.hasPLayedOnce()){
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(120);
				cwidth = 280;
				AudioPLayer.play("BossAttack");
			}
		}
		
		else if(currentAction == ATTACK){
			if(animation.hasPLayedOnce()){
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				maxSpeed = 1.6;
				cwidth = 80;
			}
		}
		animation.update();
		
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 100) {
				flinching = false;
			}
		}
		
		if(isDead()){
			AudioPLayer.stop("BossWings");
		}
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		g.setColor(Color.WHITE);
		g.setFont(new Font("Century Gothic", Font.BOLD, 10));
		g.drawString("HellDemon", 100, 60);
		
		if(MenuState.multiplayer){
			g.setColor(Color.BLUE);
			g.fillRect(160, 54, getHealth()/20, 10);
		}else{
			g.setColor(Color.BLUE);
			g.fillRect(160, 54, getHealth()/10, 10);
		}
		
		
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		if(facingRight){
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2-60), (int)(y+ymap - height / 1.5), null);
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+200), (int)(y+ymap - height / 1.5),-width,height,null);
			
		}	
			
//		g.drawRect((int)(x+xmap - width/3), (int)(y+ymap - height/4), cwidth, cheight);
	}
	
	public void setDead(){
		dead = true;
	}

}
