package model;

import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;

import view.JogoPanel;


public class Background {
	
	private BufferedImage image;
	private double moveScale;
	
	private double x;
	private double y;
	private double dx;
	private double dy;

	
	public void AddBg(String s,  double ms) {
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
			moveScale = ms;

		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void setPosition(double x, double y) {
		
		this.x = (x * moveScale) % 1536;
		this.y = (y * moveScale) % JogoPanel.ALTURA;
	
	}
	
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
		
	}
	
	public void update() {
		
//		System.out.println(x);

		if(this.x <= -2400){
			this.x = 0;
		}
		
		else if(this.x >= 1500){
			this.x = 0;
		}
		
		else{
			this.x += dx;
			this.y += dy;
		}
		

	}
	
	public void draw(Graphics2D g) {
		g.drawImage(image, (int)this.x, (int)this.y, null);

		if(this.x < 0) {
			g.drawImage(image, (int)this.x + (1536), (int)this.y, null);
		}
		if(this.x > 0) {
			g.drawImage(image, (int)this.x - (1536), (int)this.y, null);
		}
	}

}
