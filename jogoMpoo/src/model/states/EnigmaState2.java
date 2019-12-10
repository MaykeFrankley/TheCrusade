package model.states;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import controller.KeyHandler;
import controller.KeyHandler2;
import model.Background;
import model.audio.AudioPLayer;

public class EnigmaState2 extends GameState{
	
	private Background bg;
	private int currentChoice = 0;
	private Font font;
	private double redGraus = 0.1;
	private double greenGraus = 0.1;
	private double blueGraus = 0.1;
	
	private BufferedImage vermelho, verde, azul;
	
	private String[] opcoes1 = {"Vermelho", "    Verde", "Azul", "Combinar Cores"};
	private String[] opcoes2 = {"Rotacionar", "Voltar"};
	
	private boolean red;
	private boolean green;
	private boolean blue;
	
	private boolean voltar = true;
	
	int i, j, k = 0;

	public EnigmaState2(GameStateManager gsm) {
		super(gsm);
		
		try {

			bg = new Background();
			bg.AddBg("/Backgrounds/Level2/Enigma2Bg.png", 1);
			bg.setPosition(0, 0);
			
			vermelho = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/Level2/Vermelho.png"));
			verde = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/Level2/Verde.png"));
			azul = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/Level2/Azul.png"));
		
			font = new Font("Century Gothic", Font.PLAIN, 12);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AudioPLayer.load("/SFX/Enigma2Move.mp3", "Move");
		
	}

	@Override
	public void init() {}

	@Override
	public void update() {
		handleInput();
	}

	@Override
	public void draw(Graphics2D g) {
		bg.draw(g);
		
		AffineTransform red = new AffineTransform();
		red.translate(182, 86);
		red.rotate(Math.PI/redGraus);
		red.translate(-vermelho.getWidth()/2, -vermelho.getHeight()/2);
		g.drawImage(vermelho, red, null);
		
		
		AffineTransform green = new AffineTransform();
		green.translate(328, 240);
		green.rotate(Math.PI/greenGraus);
		green.translate(-verde.getWidth()/2, -verde.getHeight()/2);
		g.drawImage(verde, green, null);
		
		AffineTransform blue = new AffineTransform();
		blue.translate(463, 94);
		blue.rotate(Math.PI/blueGraus);
		blue.translate(-azul.getWidth()/2, -azul.getHeight()/2);
		g.drawImage(azul, blue, null);
		
		
		if(voltar){
			for (int i = 0; i < opcoes1.length; i++) {
				g.setFont(font);
				if(i == currentChoice){
					g.setColor(Color.YELLOW);
					
				}else{
					g.setColor(Color.WHITE);
				}
				
				g.drawString(opcoes1[i], 200+i*75, 340);
				
			}
		}
		
		else if(!voltar){
			for (int i = 0; i < opcoes2.length; i++) {
				g.setFont(font);
				if(i == currentChoice){
					g.setColor(Color.YELLOW);
					
				}else{
					g.setColor(Color.WHITE);
				}
				
				g.drawString(opcoes2[i], 200+i*75, 340);
				
			}
		}
		
		
		
	}

	
	public void select(){
		if(voltar){
			PauseState.setSalvavel(false);
			if(currentChoice == 0){
				red = true;
				voltar = false;
			}
			
			if(currentChoice == 1){
				green = true;
				voltar = false;
				
			}
			
			if(currentChoice == 2){
				blue = true;
				voltar = false;
			}
			
			if(currentChoice == 3){
				checkEnigma();
			}
		}
		
		else{
			if(currentChoice == 0){
				AudioPLayer.play("Move");
				if(red){
					i+= 2;
					if(i >= 2){
						redGraus = 2;
						if(i >= 4){
							redGraus = 1;
							if(i >= 6){
								redGraus = -2;
								if(i == 8){
									redGraus = 0.1;
									i = 0;
								}
							}
						}
					}
				}

				else if(green){
					j+= 2;
					if(j >= 2){
						greenGraus = 2;
						if(j >= 4){
							greenGraus = 1;
							if(j >= 6){
								greenGraus = -2;
								if(j == 8){
									greenGraus = 0.1;
									j = 0;
								}
							}
						}
					}
				}

				else if(blue){
					k+= 2;
					if(k >= 2){
						blueGraus = 2;
						if(k >= 4){
							blueGraus = 1;
							if(k >= 6){
								blueGraus = -2;
								if(k == 8){
									blueGraus = 0.1;
									k = 0;
								}
							}
						}
					}
				}
			}
			
			if(currentChoice == 1){
				voltar = true;
				red = false;
				green = false;
				blue = false;
				
			}
		}
		
		currentChoice = 0;
	}
	
	public void checkEnigma(){
		if(redGraus == 1.0 && greenGraus == -2.0 && blueGraus == 2.0){
			Level2State.setAcertouEnigma(true);
			gsm.setEnigma2(false);
		}
		else{
			
			JOptionPane.showMessageDialog(null, "A combinação não está correta!", "", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void handleInput() {
		if(voltar){
			if(KeyHandler2.isPressed(KeyHandler2.LEFT)){
				currentChoice--;
				if(currentChoice == -1){
					currentChoice = opcoes1.length - 1;
				}
			}
			else if(KeyHandler2.isPressed(KeyHandler2.RIGHT)){
				currentChoice++;
				if(currentChoice == opcoes1.length){
					currentChoice = 0;
				}
			}
			
			else if(KeyHandler.isPressed(KeyHandler.ENTER)){
				select();
			}
			
			else if(KeyHandler.isPressed(KeyHandler.ESCAPE)){
				Level2State.trigger = 9;
				gsm.setEnigma2(false);
			}
		}
		
		else if(!voltar){
			if(KeyHandler2.isPressed(KeyHandler2.LEFT)){
				currentChoice--;
				if(currentChoice == -1){
					currentChoice = opcoes2.length - 1;
				}
			}
			else if(KeyHandler2.isPressed(KeyHandler2.RIGHT)){
				currentChoice++;
				if(currentChoice == opcoes2.length){
					currentChoice = 0;
				}
			}
			
			else if(KeyHandler.isPressed(KeyHandler.ENTER)){
				select();
			}
		}
		
	}

	@Override
	public void handleInput2() {}

}
