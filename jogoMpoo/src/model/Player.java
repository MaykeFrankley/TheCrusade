package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import model.audio.AudioPLayer;
import view.HealthBar;

public class Player extends MapObject{
	
	private int health;
	private int maxHealth;
	private int life;
	private int maxLife;
	private boolean dead;
	
	private boolean flinching;
	private long flinchTimer;
	
	private boolean attacking1;
	private int attackDamage1;
	private int attackRange1;
	
	private boolean attacking2;
	private int attackDamage2;
	private int attackRange2;
	private int attack1Cost;
	private int maxAttack1;
	private int attacks1;
	
	private boolean airAttacking;
	private int airAttackDamage;
	private int airAttackRange;
	private boolean combo;
	private int airAttacks;
	private int maxAir;
	private int airCost;
	
	private boolean BowUsing;
	private int arrowDamage;
	private int arrow;
	private int maxArrow;
	private int arrowCost;
	private ArrayList<Arrow> arrows;
	
	private boolean usingMagic;
	private int fireDamage;
	private int fire;
	private int maxFire;
	private int fireCost;
	private ArrayList<FireBall> fireBalls;
 	
	private boolean sliding;
	private int slide;
	private int maxSlide;
	private int slideCost;
	
	private boolean reviving;
	private boolean invunerable;
	private boolean Knockback;
	private static int score;
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {3, 9, 4, 4, 4, 4, 4, 4, 4, 4, 5, 6, 6, 6, 7, 4, 2, 2};
	
	private static final int HURT = 0;
	private static final int ATTACKBOW = 1;
	private static final int JUMP1 = 2;
	private static final int JUMP2 = 3;
	private static final int AIRATTACK = 4;
	private static final int CROUCH1 = 5;
	private static final int CROUCH2 = 6;
	private static final int IDLE1 = 7;
	private static final int IDLE2 = 8;
	private static final int IDLE3 = 9;
	private static final int ATTACK1 = 10;
	private static final int WALKING1 = 11;
	private static final int WALKING2 = 12;
	private static final int ATTACK2 = 13;
	private static final int DIE = 14;
	private static final int MAGICK = 15;
	private static final int SLIDE = 16;
	private static final int FALLING = 17;
	
	public Player(TileMap tm){
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 18;
		cheight = 30;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		health = maxHealth = 500;	
		life = maxLife = 3;
		
		
		fire = maxFire = 2500;
		fireDamage = 5;
		fireCost = 500;
		fireBalls = new ArrayList<FireBall>();
		
		arrowDamage = 2;
		arrow = maxArrow = 1000;
		arrowCost = 100;
		arrows = new ArrayList<Arrow>();
		
		slide = maxSlide = 1400;
		slideCost = 400;
		
		attackDamage1 = 10;
		attackRange1 = 30;
		
		attacks1 = maxAttack1 = 1400;
		attack1Cost = 400;
		
		attackDamage2 = 30;
		attackRange2 = 30;
		

		airAttackDamage = 8;
		airAttackRange = 80;
		
		airAttacks = maxAir = 1400;
		airCost = 400;
		
		// Carregar Sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(
					"/TileSetsAndSprites/PlayerTileset.png"));
			
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < 18; i++){
				BufferedImage[] bi = new BufferedImage[numFrames[i]];
				for(int j = 0; j < numFrames[i]; j++){
					bi[j] = spritesheet.getSubimage(j*width, i*height, width, height);
				}
				
				sprites.add(bi);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		currentAction = IDLE1;
		animation.setFrames(sprites.get(IDLE1));
		animation.setDelay(400);

	}
	
	public int getHealth() {return health;}
	public int getMaxHealth() {return maxHealth;}
	public int getLife(){return life;}
	public int getMaxLife(){return maxLife;}
	public int getFire(){return fire;}
	public int getMaxFire(){return maxFire;}
	public int getArrows(){return arrow;}
	public int getMaxArrow(){return maxArrow;}
	public int getSlide(){return slide;}
	public int getAirAttakcs(){return airAttacks;}
	public int getAttacks1(){return attacks1;}
	public boolean isDead(){return dead;}
	public boolean isReviving(){return reviving;}
	public boolean isInvunerable(){return invunerable;}
	
	public void setArrows(int a){
		arrow += a;
		if(arrow > maxArrow) arrow = maxArrow;
	}
	
	public void addHealth(int h){
		health += h;
		if(health > maxHealth) health = maxHealth;
	}
	
	public void setHealth(int h){
		health = h;
		if(health > maxHealth) health = maxHealth;
	}
	
	public void setLife(int l){
		life += l;
		if(life > maxLife) life = maxLife;
	}
	
	public void setJumping(boolean b){
		if(Knockback) return;
		jumping = b;
	}
	
	public void setMagicing(boolean b){
		usingMagic = b;
	}
	
	public void setUsingBow(boolean b){
		BowUsing = b;
	}
	
	public void setAttacking1(boolean b){
		attacking1 = b;
	}
	public void setAttacking2(boolean b){
		attacking2 = b;
	}
	
	public void setSlide(boolean b){
		if(slide >= slideCost){
			sliding = b;
		}
	}
	
	public void setAirAttack(){
		if((jumping || falling) && airAttacks >= airCost){
			airAttacking = true;
		}
		combo = true;
	}
	
	public void setReviving(boolean b){
		reviving = b;
	}
	
	public void setDead(boolean b){
		dead = b;
	}
	
	public void increaseScore(int score) {
		Player.score += score; 
	}
	
	public void setScore(int score){
		Player.score = score;
	}
	
	public static int getScore() { return score; }
	
	public void checkAttackBoss(Enemy boss){
			
		if(attacking1){
			if(facingRight){
				if(
					boss.getx() > x &&
					boss.getx() < x + attackRange1 && 
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2
					) {
					if(boss.getName() == "HellBeast"){
						dx = 6;
						boss.hit(attackDamage1);

					}
					else{
						boss.hit(attackDamage1);
					}

				}					
			}
			else{
				if(
					boss.getx() < x &&
					boss.getx() > x - attackRange1 &&
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2
					) {

					if(boss.getName() == "HellBeast"){
						dx = -6;
						boss.hit(attackDamage1);

					}
					else{
						boss.hit(attackDamage1);
					}

				}
			}
		}

		if(attacking2){
			if(facingRight){
				if(
					boss.getx() > x &&
					boss.getx() < x + attackRange2 && 
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2
					) {

					if(boss.getName() == "HellBeast"){
						dx = 6;
						boss.hit(attackDamage1);

					}
					else{
						boss.hit(attackDamage1);
					}
				}
			}
			else{
				if(
					boss.getx() < x &&
					boss.getx() > x - attackRange2 &&
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2
					) {

					if(boss.getName() == "HellBeast"){
						dx = -6;
						boss.hit(attackDamage1);

					}
					else{
						boss.hit(attackDamage1);
					}
				}
			}
		}

		if(airAttacking){
			if(facingRight){
				if(intersects(boss) || boss.getx() > x &&
					boss.getx() < x + airAttackRange && 
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2)
				{
					boss.hit(airAttackDamage);
				}
			}
			else{
				if(intersects(boss) || boss.getx() > x &&
					boss.getx() < x + airAttackRange && 
					boss.gety() > y - height / 2 &&
					boss.gety() < y + height / 2)
				{
					boss.hit(airAttackDamage);
				}
			}
		}

		//Arrows
		for(int j = 0; j < arrows.size(); j++){
			if(arrows.get(j).intersects(boss)){
				boss.hit(arrowDamage);
				arrows.get(j).setHit();
			}
		}


		//fireballs
		for(int j = 0; j < fireBalls.size(); j++){
			if(fireBalls.get(j).intersects(boss)){
				boss.hit(fireDamage);
				fireBalls.get(j).setHit();
			}
		}


	}
	
	public void checkAttack(ArrayList<Enemy> enemies){
		
		for(int i = 0; i < enemies.size(); i++){
			
			Enemy e = enemies.get(i);
						
			if(attacking1){
				if(facingRight){
					if(
						e.getx() > x &&
						e.getx() < x + attackRange1 && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						if(e.getName() == "HellBeast"){
							e.hit(attackDamage1);
						}
						else{
							e.hit(attackDamage1);
							e.dx = 6;
						}
					}					
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - attackRange1 &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						if(e.getName() == "HellBeast"){
							e.hit(attackDamage1);
						}
						else{
							e.hit(attackDamage1);
							e.dx = -6;
						}
					}
				}
			}
			
			if(attacking2){
				if(facingRight){
					if(
						e.getx() > x &&
						e.getx() < x + attackRange2 && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						if(e.getName() == "HellBeast"){
							e.hit(attackDamage2);
						}
						else{
							e.hit(attackDamage2);
							e.dx = 6;
						}

					}
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - attackRange2 &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						if(e.getName() == "HellBeast"){
							e.hit(attackDamage2);

						}
						else{
							e.hit(attackDamage2);
							e.dx = -6;
						}
					}
				}
			}
			
			if(airAttacking){
				if(facingRight){
					if(
						e.getx() > x &&
						e.getx() < x + airAttackRange && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(airAttackDamage);
						e.dx = 6;

					}
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - airAttackRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(airAttackDamage);
						e.dx = -6;
					}
				}
			}
			
			//Arrows
			for(int j = 0; j < arrows.size(); j++){
				if(arrows.get(j).intersects(e)){
					arrows.get(j).setHit();
					if(facingRight){
						if(e.getName() == "HellBeast"){
							e.dx = 0;
							e.hit(arrowDamage);
						}
						else{
							e.dx = 6;
							e.hit(arrowDamage+10);
						}
						
					}else{
						if(e.getName() == "HellBeast"){
							e.dx = 0;
							e.hit(arrowDamage);
						}else{
							e.dx = -6;
							e.hit(arrowDamage+10);
						}
					}
				}
			}
			
			
			//fireballs
			for(int j = 0; j < fireBalls.size(); j++){
				if(fireBalls.get(j).intersects(e)){
					fireBalls.get(j).setHit();
					if(facingRight){
						if(e.getName() == "HellBeast"){
							e.dx = 0;
						}
						else{
							e.dx = 6;
							e.hit(fireDamage+10);
						}
						
					}else{
						if(e.getName() == "HellBeast"){
							e.hit(fireDamage);
							e.dx = 0;
						}else{
							e.dx = -6;
							e.hit(fireDamage);
						}
					}
					
					
				}
			}
			
			if(intersects(e)){
				hit(e.getDamage());
			}		
		}		
	}
	
	public void checkItem(ArrayList<Item> item){
		for(int i = 0; i < item.size(); i++){
			
			Item e = item.get(i);
			
			if(attacking1 && item.get(i).isDestructible()){
				if(facingRight) {
					if(
						e.getx() > x &&
						e.getx() < x + attackRange1 && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();

					}
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - attackRange1 &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();;
					}
				}
			}
			
			if(attacking2 && item.get(i).isDestructible()){
				if(facingRight){
					if(
						e.getx() > x &&
						e.getx() < x + attackRange2 && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();;

					}
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - attackRange2 &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();;
					}
				}
			}
			
			if(airAttacking && item.get(i).isDestructible()){
				if(facingRight){
					if(
						e.getx() > x &&
						e.getx() < x + airAttackRange && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();

					}
				}
				else{
					if(
						e.getx() < x &&
						e.getx() > x - airAttackRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.setHit();
					}
				}
			}
			
		}	
		
	}

	public void hit(int damage){
		if(flinching || airAttacking || sliding || invunerable || combo) return;
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0 && life > 0){
			health = maxHealth;
			life--;
			flinchTimer = System.nanoTime();
			invunerable = true;
		}
		Knockback = true;
		if(facingRight) dx = -2;
		else dx = 2;
		dy = -3;
		
		if(life < 0) life = 0;
		if(life == 0 && health == 0) dead = true;
		flinching = true;
		flinchTimer = System.nanoTime();
		AudioPLayer.play("Hit");
		
	}

	
	private void getNextPosition(){
		
		if(Knockback) {
			dy += fallSpeed * 2;
			if(!falling) Knockback = false;
			return;
		}
		
		//movement
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
		
		//jump
		if(jumping && !falling){
			AudioPLayer.play("playerjump");
			dy = jumpStart;
			falling = true;
		}
		
		
		if(jumping || falling){
			setAttacking1(false);
			setAttacking2(false);
			setMagicing(false);
			setUsingBow(false);
			setSlide(false);
		}
		
		//fall
		if(falling){
			dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			if(dy > maxFallSpeed) dy = maxFallSpeed;
					
		}

		if(sliding){
			if(left){
				dx -= moveSpeed+0.5;
				if(dx < -maxSpeed-2.5){
					dx = -maxSpeed-2.5;
				}
			}
			else if(right){
				dx += moveSpeed+0.5;
				if(dx > maxSpeed+2.5){
					dx = maxSpeed+2.5;
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
		}
		
		if(currentAction == ATTACK2 || currentAction == ATTACKBOW || currentAction == MAGICK || currentAction == ATTACK1){
			dx = 0;
		}
		
		if(isDead()){
			invunerable = true;
		}
		
		
		if(currentAction == ATTACK1 || currentAction == ATTACK2 || currentAction == AIRATTACK){
			setMagicing(false);
		}
		
		if(currentAction == SLIDE){
			setAttacking1(false);
			setAttacking2(false);
			setUsingBow(false);
			setMagicing(false);
		}
		
		if(currentAction == ATTACKBOW){
			setAttacking1(false);
			setAttacking2(false);
			setMagicing(false);
		}
		
		if(airAttacking){
			dy = -1;
			fallSpeed = 0.1;
			if(right)dx = 3.5;
			if(left)dx = -3.5;
		}else{
			fallSpeed = 0.15;
		}
		
		if(attacking1){
			if(attacks1 >= attack1Cost){
				combo = true;
				if(right)dx = 3.5;
				if(left)dx = -3.5;
			}
		}	
		
	}
	
	public void update(){
		
		
		//update posicao
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(currentAction == ATTACK1){
			if(animation.hasPLayedOnce()) attacking1 = false;
		}
		
		if(currentAction == ATTACK2){
			if(animation.hasPLayedOnce()) attacking2 = false;
		}
		
		if(currentAction == SLIDE){
			if(animation.hasPLayedOnce()) {
				slide -= slideCost;
				sliding = false;
			}
		}
		
		if(currentAction == ATTACK1){
			if(animation.hasPLayedOnce()){
				attacking1 = false;
				if(attacks1 >= attack1Cost)attacks1 -= attack1Cost;
			}
		}
		
		if(currentAction == ATTACKBOW){
			if(animation.hasPLayedOnce()) BowUsing = false;
		}
		
		if(currentAction == MAGICK){
			if(animation.hasPLayedOnce()) usingMagic = false;
		}
		
		if(currentAction == AIRATTACK){
			if(animation.hasPLayedOnce()){
				airAttacks -= airCost;
				airAttacking = false;
			}
		}
		
		if(currentAction == JUMP1){
			if(animation.hasPLayedOnce()) jumping = false;
		}
		
		if(currentAction == JUMP2){
			if(animation.hasPLayedOnce()) jumping = false;
		}
		
		if(currentAction == DIE){
			if(animation.hasPLayedOnce()){
				animation.setFrames(sprites.get(CROUCH2));
				animation.setDelay(500);
			}
		}	
		
		//Incremets
		slide += 2;
		if(slide > maxSlide) slide = maxSlide;
		if(slide < 0) slide = 0;
	
		airAttacks += 2;
		if(airAttacks > maxAir) airAttacks = maxAir;
		if(airAttacks < 0) airAttacks = 0;
		
		attacks1 += 1;
		if(attacks1 > maxAttack1) attacks1 = maxAttack1;
		if(attacks1 < 0) attacks1 = 0;
		
		fire += 2;
		if(fire > maxFire) fire = maxFire;
		if(usingMagic && currentAction != MAGICK){
			if(fire > fireCost){
				fire -= fireCost;
				FireBall fb = new FireBall(tileMap, facingRight);
				fb.setPosition(x, y);
				fireBalls.add(fb);
			}
		}
		//
		
		if(arrow > maxArrow) arrow = maxArrow;
		if(BowUsing && currentAction != ATTACKBOW){
			if(arrow >= arrowCost){
				arrow -= arrowCost;
				Arrow a = new Arrow(tileMap, facingRight, false);
				a.setPosition(x, y);
				arrows.add(a);
			}
		}
		
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).update();
			if(fireBalls.get(i).shouldRemove()){
				fireBalls.remove(i);
				i--;
			}
		}
		
		for(int i = 0; i < arrows.size(); i++){
			arrows.get(i).update();
			if(arrows.get(i).shouldRemove()){
				arrows.remove(i);
				i--;
			}
		}
		
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 1000) {
				flinching = false;
			}
		}
		
		if(invunerable) {
			right = false;
			left = false;
			dx = 0;
			long elapsed = 
					(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 3000){
				invunerable = false;
				flinching = true;
				flinchTimer = System.nanoTime();
			}
			
		}
		
		//set animacao
		
		if(attacking1){
			if(currentAction != ATTACK1){
				currentAction = ATTACK1;
				animation.setFrames(sprites.get(ATTACK1));
				animation.setDelay(50);
				AudioPLayer.play("attack");
				width = 30;
			}
		}
		else if(attacking2){
			if(currentAction != ATTACK2){
				currentAction = ATTACK2;
				animation.setFrames(sprites.get(ATTACK2));
				animation.setDelay(100);
				width = 30;
				AudioPLayer.play("attack");
				
			}
		}
		
		else if(airAttacking){
			if(currentAction != AIRATTACK){
				currentAction = AIRATTACK;
				animation.setFrames(sprites.get(AIRATTACK));
				animation.setDelay(50);
				width = 30;
				AudioPLayer.play("attack");
			}
		}
		
		else if(dy > 0){
			if(currentAction != FALLING){
				currentAction = FALLING;
				animation.setFrames(sprites.get(FALLING));
				animation.setDelay(200);
				width = 30;
			}
		}
		else if(sliding){
			if(currentAction != SLIDE){
				AudioPLayer.play("Slide");
				currentAction = SLIDE;
				animation.setFrames(sprites.get(SLIDE));
				animation.setDelay(200);
				width = 30;
			}
		}
		else if(dy < 0){
			if(currentAction != JUMP1){
				currentAction = JUMP1;
				animation.setFrames(sprites.get(JUMP1));
				animation.setDelay(50);
				width = 30;
			}
			else if(currentAction != JUMP2){
				currentAction = JUMP1;
				animation.setFrames(sprites.get(JUMP2));
				animation.setDelay(50);
				width = 30;
			}
			
		}
		else if(BowUsing){
			if(currentAction != ATTACKBOW){
				AudioPLayer.play("Bow");
				currentAction = ATTACKBOW;
				animation.setFrames(sprites.get(ATTACKBOW));
				animation.setDelay(60);
				width = 30;
			}
		}
		
		else if(usingMagic){
			if(currentAction != MAGICK){
				AudioPLayer.play("FireBall");
				currentAction = MAGICK;
				animation.setFrames(sprites.get(MAGICK));
				animation.setDelay(100);
				width = 30;
			}
		}
		
		else if(left || right){
			if(currentAction != WALKING2){
				currentAction = WALKING2;
				animation.setFrames(sprites.get(WALKING2));
				animation.setDelay(100);
				width = 30;
			}
		}
		else if(dead || invunerable){
			if(currentAction != DIE){
				currentAction = DIE;
				animation.setFrames(sprites.get(DIE));
				animation.setDelay(250);
				width = 30;
			}
		}
		
		else if(reviving){
			if(currentAction != HURT){
				currentAction = HURT;
				animation.setFrames(sprites.get(HURT));
				animation.setDelay(150);
				dx = 0;
			}
		}
		
		else if(Knockback){
			if(currentAction != HURT){
				currentAction = HURT;
				animation.setFrames(sprites.get(HURT));
				animation.setDelay(150);
			}
		}
		
		else{
			if(currentAction != IDLE1){
				currentAction = IDLE1;
				animation.setFrames(sprites.get(IDLE1));
				animation.setDelay(400);
				width = 30;
			}
		}
		
		if(!falling) combo = false;
		
		animation.update();
		
		if(currentAction != MAGICK && currentAction != ATTACKBOW){
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		
		for(int i = 0; i < fireBalls.size(); i++){
			fireBalls.get(i).draw(g);
		}
		
		for (int i = 0; i < arrows.size(); i++) {
			arrows.get(i).draw(g);
		}
		
		//draw jogador
		
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		if(facingRight){
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2), (int)(y+ymap - height / 2), width, 35, null);
			
		}else{
			g.drawImage(animation.getImage(), (int)(x+xmap - width / 2+width), (int)(y+ymap - height / 2),-width,35,null);
		}
		
		//Barra de vida
		HealthBar playerBarra = new HealthBar(tileMap, "Player", health/20);
		playerBarra.setPosition(x-10, y);
		playerBarra.draw(g);
	}
	
}
