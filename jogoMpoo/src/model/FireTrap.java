package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FireTrap extends Enemy{
	
	private ArrayList<BufferedImage[]> sprites;
	private final int[] numFrames = {4, 4, 3};
	
	private static final int IDLE = 0;

	public FireTrap(TileMap tm) {
		super(tm);
		
		width = 128;
		height = 64;
		cwidth = 128;
		cheight = 32;
		
		health = 1000;
		damage = 250;
		
		try {

			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/FireTrap.png"));

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
		
	
	}
	
	public void update(){
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		animation.update();
	}
	
	public void draw(Graphics2D g){
		setMapPosition();
		g.drawImage(animation.getImage(), (int)(x+xmap - width / 1+width), (int)(y+ymap - height / 2),-width,height,null);

	}

}
