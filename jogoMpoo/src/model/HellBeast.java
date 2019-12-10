package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.Level2State;
import model.states.MenuState;
import view.HealthBar;

public class HellBeast extends Enemy{
	
	private int fireDamage;
	private int fire;
	private int maxFire;
	private int fireCost;
	private ArrayList<FireBall> fireBalls;
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {6, 2, 4, 4};
	private BufferedImage[] hitSprites;
	
	private static final int IDLE = 0;
	private static final int APPEAR1 = 1;
	private static final int APPEAR2 = 2;
	private static final int ATTACK = 3;
	
	private boolean hit;
	private long flinchTimer;
	private Animation animation2;

	public HellBeast(TileMap tm) {
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
			health = maxHealth = 200;
		}else{
			health = maxHealth = 100;
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
			
			BufferedImage spritesheet2 = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/EnemyHit.png"));
			hitSprites = new BufferedImage[24];
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet2.getSubimage(i*32, 0, 32, height);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation2 = new Animation();
		
		currentAction = APPEAR1;
		animation.setFrames(sprites.get(APPEAR1));
		animation.setDelay(150);
		
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
	
	public void update(){
			
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(right && x > Level2State.player.getx()){
			right = false;
			left = true;
			facingRight = false;

		}
		else if(left && x < Level2State.player.getx()){
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
		
		if(Level2State.player.gety() == y && currentAction == IDLE || MenuState.multiplayer && Level2State.player2.gety() == y){
			if(fire == fireCost){
				fire -= fireCost;
				currentAction = ATTACK;
				animation.setFrames(sprites.get(ATTACK));
				animation.setDelay(200);
				AudioPLayer.play("HellBeast");
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}					
		}
		
		if(hit) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 2000) {
				hit = false;
			}
		}
		
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).update();
			
			if(fireBalls.get(i).intersects(Level2State.player)){
				Level2State.player.hit(fireDamage);
			}
			if(MenuState.multiplayer && fireBalls.get(i).intersects(Level2State.player2)){
				Level2State.player2.hit(fireDamage);
			}
			if(fireBalls.get(i).shouldRemove()){
				fireBalls.remove(i);
				i--;
			}
		}
		

		animation.update();
		animation2.update();
	}
	
	public void draw(Graphics2D g){
	
		setMapPosition();
			
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).draw(g);
		}
		
		if(facingRight){
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height/1.5),-width,height,null);
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2), (int)(y+ymap - height/1.5), null);
		}
		
		if(hit){
			g.drawImage(animation2.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,35,null);
		}
		
		HealthBar bossBarra = new HealthBar(tileMap, "Enemy", health);
		bossBarra.setPosition(x-20, y-35);
		bossBarra.draw(g);
	}
	
	public void setDead(){
		dead = true;
	}
	


}
