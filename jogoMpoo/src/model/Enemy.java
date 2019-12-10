package model;

import model.audio.AudioPLayer;

public class Enemy extends MapObject{
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;

	public Enemy(TileMap tm) {
		super(tm);
		AudioPLayer.load("/SFX/EnemyHit.mp3", "HitSound");
	}
	
	public boolean isDead(){return dead;}
	
	public int getDamage(){return damage;}
	
	public int getHealth(){return health;}
	
	public void hit(int damage){
		AudioPLayer.play("HitSound");
		if(this.dead) return ;
		this.health -= damage;
		if(this.health < 0) this.health = 0;
		if(this.health == 0) this.dead = true;
	}
	
	public void setDead(boolean b){
		dead = b;
	}
	
	public void update(){}
	
}
