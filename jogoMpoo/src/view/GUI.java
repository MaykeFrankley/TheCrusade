package view;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import model.Player;
import model.Player2;
import model.states.MenuState;

public class GUI {
	
	private Player player;
	private Player2 player2;
	
	private BufferedImage image, image2;
	private Font font;
	
	public static int min = 0;
	public static int sec = 0;
	public static int s;
	public static int m;
	
	public static long Time;
		

	public GUI(Player p, Player2 p2){
		player = p;
		player2 = p2;
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/HUD.png"));
			if(MenuState.multiplayer){
				image2 = ImageIO.read(getClass().getResourceAsStream("/TileSetsAndSprites/HUD2.png"));
			}
			font = new Font("Arial", Font.PLAIN, 12);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
						
	}
	
	public static void setGameTime(int time){
		Time = time;
	}
	
	public static String setTime(){
		min = (int) (Time / 3600);
		sec = (int) ((Time % 3600) / 60);
		return sec < 10 ? min + ":0" + sec : min + ":" + sec;
	}

	
	public void draw(Graphics2D g){
		
		g.setColor(Color.WHITE);
		g.drawImage(image, 0, 0, null);
		g.setFont(font);
		g.drawString(player.getLife()+"/"+player.getMaxLife(), 6, 30);
		g.drawString(player.getArrows()/100+"/"+player.getMaxArrow()/100, 45, 35);
		
		if(MenuState.multiplayer){
			g.drawImage(image2, 260, 0, null);
			g.drawString(player2.getLife()+"/"+player2.getMaxLife(), 266, 30);
			g.drawString(player2.getArrows()/100+"/"+player2.getMaxArrow()/100, 305, 35);
		}
		
		Time++;
		
		g.setFont(new Font("Tahoma", Font.PLAIN, 15));
		g.drawString(setTime(), 605, 25);
		g.drawString("Score: "+Player.getScore(), 510, 25);

			
		g.setColor(Color.YELLOW);
		g.fillRect(173, 2, player.getFire() / 94, 10);
		
		g.setColor(Color.BLUE);
		g.fillRect(146, 2, player.getSlide() / 55, 10);
		
		g.setColor(Color.WHITE);
		g.fillRect(228, 2, player.getAirAttakcs() / 80, 10);
		
		g.fillRect(92, 2, player.getAttacks1() / 55, 10);
		
		if(MenuState.multiplayer){
			g.setColor(Color.YELLOW);
			g.fillRect(433, 2, player2.getFire() / 94, 10);
			
			g.setColor(Color.BLUE);
			g.fillRect(406, 2, player2.getSlide() / 55, 10);
		}
		
		
	}
	

}
