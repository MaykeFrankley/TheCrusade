package model;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

public class DeadAnimation {
	
	private int x;
	private int y;
	private int xmap;
	private int ymap;
	
	private int width;
	private int height;
	
	private Animation animation;
	private BufferedImage[] sprites;
	
	private boolean remove;
	
	public DeadAnimation(int largura, int altura, int x, int y, int nFrames, String s){
		
		this.x = x;
		this.y = y;
		
		width = largura;
		height = altura;
		
		try {
			
			BufferedImage spritesheet = ImageIO.read(getClass().getResourceAsStream(s));
			sprites = new BufferedImage[nFrames];
			
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = spritesheet.getSubimage(i * width, 0, width, height);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(130);
	}
	
	public void update(){
		animation.update();
		if(animation.hasPLayedOnce()) remove = true;
	}
	
	public boolean shouldRemove(){return remove;}
	
	public void setMapPosition(int x, int y){
		this.xmap = x;
		this.ymap = y;
	}
	
	public void draw(Graphics2D g){
		g.drawImage(animation.getImage(), x + xmap - width / 2, (int)(y + ymap - height / 1.5), null);
	}
	

}
