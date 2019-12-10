package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import model.states.Level2State;
import model.states.MenuState;
import view.HealthBar;

public class Skeleton extends Enemy{
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {8, 6};
	private BufferedImage[] hitSprites;
	
	private static final int WALK = 0;
	private static final int RISE = 1;
	
	private boolean hit;
	private long flinchTimer;
	private Animation animation2;

	public Skeleton(TileMap tm) {
		super(tm);
		
		moveSpeed = 0.3;
		maxSpeed = 0.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		
		width = 44;
		height = 52;
		cwidth = 20;
		cheight = 20;
		
		if(MenuState.multiplayer){
			health = maxHealth = 100;
		}else{
			health = maxHealth = 50;
		}
		
		damage = 100;
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/SkeletonSprite.png"));

			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 2; i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++){
					bi[j] = spritesheet.getSubimage(j*width, i*height, width, height);
				}

				sprites.add(bi);
			}
			
			BufferedImage spritesheet2 = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/EnemyHit.png"));
			hitSprites = new BufferedImage[24];
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet2.getSubimage(i*32, 0, 32, 32);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation2 = new Animation();
		
		currentAction = RISE;
		animation.setFrames(sprites.get(RISE));
		animation.setDelay(100);
		
		right = true;
		facingRight = true;
		
		animation2 = new Animation();
		animation2.setFrames(hitSprites);
		animation2.setDelay(10);
		
		AudioPLayer.load("/SFX/Skeleton.mp3", "Skeleton");
		AudioPLayer.loop("Skeleton");
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
		
		if(falling){
			dy += fallSpeed;
		}
					
	}
	
	public void update(){
		if(dead){
			AudioPLayer.stop("Skeleton");
		}
		
		getNextPosition();
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
		
		
		if(currentAction == RISE){
			if(animation.hasPLayedOnce()){
				currentAction = WALK;
				animation.setFrames(sprites.get(WALK));
				animation.setDelay(200);
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
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,height-10,null);
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2), (int)(y+ymap - height / 2), width,height-10, null);
		}
		
		if(hit){
			g.drawImage(animation2.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,35,null);
		}
		
		HealthBar bossBarra = new HealthBar(tileMap, "Enemy", health);
		bossBarra.setPosition(x-10, y-25);
		bossBarra.draw(g);
	}
	
	public void setDead(){
		dead = true;
	}
		

}
