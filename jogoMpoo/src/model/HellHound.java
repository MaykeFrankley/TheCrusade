package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.MenuState;
import view.HealthBar;

public class HellHound extends Enemy{
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {12, 5, 6};
	private BufferedImage[] hitSprites;
	
	private static final int WALK = 0;
	private static final int RUN = 1;
	private static final int ATTACK = 2;
	
	private boolean hit;
	private long flinchTimer;
	private Animation animation2;

	public HellHound(TileMap tm) {
		super(tm);
		
		moveSpeed = 0.3;
		maxSpeed = 0.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		width = 64;
		height = 32;
		cwidth = 26;
		cheight = 18;
		
		if(MenuState.multiplayer){
			health = maxHealth = 100;
		}else{
			health = maxHealth = 50;
		}
		
		damage = 100;

		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/HellHound.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 3; i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++){
					bi[j] = spritesheet.getSubimage(j*width, i*height, width, height);
				}
				sprites.add(bi);
			}
			
			BufferedImage spritesheet2 = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/EnemyHit.png"));
			hitSprites = new BufferedImage[24];
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet2.getSubimage(i*32, 0, 32, height);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = WALK;
		animation.setFrames(sprites.get(WALK));
		animation.setDelay(100);
		
		right = true;
		facingRight = true;		
		
		animation2 = new Animation();
		animation2.setFrames(hitSprites);
		animation2.setDelay(10);
		
	}
	
	public void hit(int damage){
		AudioPLayer.play("HitSound");
		if(this.dead) return ;
		this.health -= damage;
		if(this.health < 0) this.health = 0;
		if(this.health == 0) this.dead = true;
		flinchTimer = System.nanoTime();
		hit = true;
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
	
	public void attack(){
		setJumping(true);
	}
	
	public void update(){
		
		int num1 = ThreadLocalRandom.current().nextInt(800, 1500);
		
		int num2 = ThreadLocalRandom.current().nextInt(100, 300);
		
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(right && x > num1){
			right = false;
			left = true;
			facingRight = false;

		}
		else if(left && x < num2){
			right = true;
			left = false;
			facingRight = true;

		}

		
		if(health < maxHealth && currentAction == WALK){
			if(currentAction != RUN){
				currentAction = RUN;
				animation.setFrames(sprites.get(RUN));
				animation.setDelay(100);
	
				maxSpeed = 2;
			}
		}
		
		if(jumping){
			if(currentAction != ATTACK){
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(200);
				width = 64;
				maxSpeed = 3;
			}
		}
		
		if(currentAction == ATTACK){
			if(animation.hasPLayedOnce()){
				currentAction = RUN;
				animation.setFrames(sprites.get(RUN));
				animation.setDelay(100);
				maxSpeed = 1.0;
			}
		}
		
		if(hit) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 2000) {
				hit = false;
			}
		}


		animation.update();
		animation2.update();
		
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		
		if(facingRight){
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,height,null);
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2), (int)(y+ymap - height / 2), null);
		}

		if(hit){
			g.drawImage(animation2.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,35,null);
		}
		
		HealthBar inimigoBarra = new HealthBar(tileMap, "Enemy", health);
		inimigoBarra.setPosition(x-10, y-20);
		inimigoBarra.draw(g);
	}
	
	public void setDead(){
		dead = true;
	}
	
}
