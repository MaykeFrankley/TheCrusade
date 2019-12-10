package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Item extends MapObject{
	
	private boolean temVida;
	private boolean temFlecha;
	private boolean temPotion;
	private boolean hit;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	private boolean destructible;


	public Item(TileMap tm, String s, int numFrames1, int numFrames2, boolean destructible) {
		super(tm);
		
		width = 32;
		height = 32;
		cwidth = 18;
		cheight = 18;
		
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(s));
			sprites = new BufferedImage[numFrames1];
			for(int i = 0; i < sprites.length; i++){
				sprites[i] = spritesheet.getSubimage(i*width, 0, width, height);
			}
			
			hitSprites = new BufferedImage[numFrames2];
			for (int i = 0; i < hitSprites.length; i++) {
				hitSprites[i] = spritesheet.getSubimage(i*width, height, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);

		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		
		this.destructible = destructible;
		
	}
	
	
	public void setHit(){
		if(hit) return;
		hit = true;
		
		animation.setFrames(hitSprites);
		animation.setDelay(50);

	}
	
	public boolean shouldRemove(){return remove;}
	public boolean isDestructible(){return destructible;}
	
	
	public boolean tempotion(){
		return temPotion;
	}
	
	public void setTemPotion(boolean b){
		temPotion = b;
	}
	
	public boolean TemVida() {
		return temVida;
	}

	public void setTemVida(boolean temVida){
		this.temVida = temVida;
	}

	public boolean TemFlecha() {
		return temFlecha;
	}

	public void setTemFlecha(boolean temFlecha) {
		this.temFlecha = temFlecha;
	}


	public void update(){

		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		animation.update();
		if(hit && animation.hasPLayedOnce()){
			remove = true;
		}
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		super.draw(g);
	}
	
	

}
