package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class NPC extends MapObject{
	
	private BufferedImage[] sprites;

	public NPC(TileMap tm, String s, int numFrames, int largura, int altura) {
		super(tm);
		
		width = largura;
		height = altura;
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(s));
			sprites = new BufferedImage[numFrames];
			for(int i = 0; i < sprites.length; i++){
				sprites[i] = spritesheet.getSubimage(i*width, 0, width, height);
			}
			
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(150);

		}
		 catch (Exception e) {
			e.printStackTrace();
		}
		
		
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
