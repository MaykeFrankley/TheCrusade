package model;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.MenuState;
import view.HealthBar;

public class Ghost extends Enemy{
	
	private ArrayList<BufferedImage[]> sprites;
	private BufferedImage[] hitSprites;
	private final int[] numFrames = {6, 7, 4, 7};
	
	private static final int APPEAR = 0;
	private static final int IDLE = 1;
	private static final int ATTACK = 2;
	
	private boolean hit;
	private long flinchTimer;
	private Animation animation2;

	public Ghost(TileMap tm) {
		super(tm);
		
		moveSpeed = 0.6;
		maxSpeed = 0.9;
		stopSpeed = 0.001;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		
		width = 32;
		height = 32;
		cwidth = 18;
		cheight = 40;
		
		if(MenuState.multiplayer){
			health = maxHealth = 80;
		}else{
			health = maxHealth = 40;
		}
		
		damage = 100;

		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/GhostSprite.png"));

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
				hitSprites[i] = spritesheet2.getSubimage(i*width, 0, width, height);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation2 = new Animation();
		
		currentAction = APPEAR;
		animation.setFrames(sprites.get(APPEAR));
		animation.setDelay(200);
		left = true;
		facingRight = false;
		
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
		
		if(falling){
			dy += fallSpeed;
		}
		
		
	}
	
	public void update(){
		
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(right && x > 400){
			right = false;
			left = true;
			facingRight = false;

		}
		else if(left && x < 50){
			right = true;
			left = false;
			facingRight = true;

		}

		if(currentAction == APPEAR){
			dx = 0;
			if(animation.hasPLayedOnce()){
				currentAction = IDLE;
				animation.setFrames(sprites.get(IDLE));
				animation.setDelay(200);
			}
		}
		
		else if(health < maxHealth && currentAction == IDLE){
			currentAction = ATTACK;
			animation.setFrames(sprites.get(ATTACK));
			animation.setDelay(70);
			left = false;
			right = true;
			facingRight = true;
			maxSpeed = 2.6;
		}
		
		if(hit) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 500) {
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
			g.drawImage(animation2.getImage().getScaledInstance(128, 128, Image.SCALE_SMOOTH), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,35,null);
		}
		
		HealthBar inimigoBarra = new HealthBar(tileMap, "Enemy", health);
		inimigoBarra.setPosition(x-10, y-20);
		inimigoBarra.draw(g);
	}
	
	public void setDead(){
		dead = true;
	}
	
	

}
