package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class Rain extends MapObject{
	
	private BufferedImage[] sprites;

	public Rain(TileMap tm) {
		super(tm);
		
		width = 640;
		height = 360;
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/RainSprite.png"));
			sprites = new BufferedImage[5];
			for(int i = 0; i < sprites.length; i++){
				sprites[i] = spritesheet.getSubimage(i*width, 0, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(70);

		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		
		facingRight = true;
	}
	
	public void update(){
		setPosition(xtemp, ytemp);
		animation.update();
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		super.draw(g);
	}


	

}
