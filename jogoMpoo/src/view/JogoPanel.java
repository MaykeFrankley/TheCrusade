package view;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.*;

import javax.swing.JPanel;
import controller.KeyHandler;
import controller.KeyHandler2;
import model.states.GameStateManager;

@SuppressWarnings("serial")
public class JogoPanel extends JPanel implements Runnable, KeyListener{
	
	public static final int LARGURA = 640;
	public static final int ALTURA = 360;
	public static final int SCALE = 2;
	
	private Thread thread;
	private boolean rodando;
	private int FPS = 60;
	private long targetTime = 1000 / FPS;
	
	private BufferedImage image;
	private Graphics2D g;
	
	private GameStateManager gsm;
	
	public JogoPanel(){
		super();
		setPreferredSize(new Dimension(LARGURA * SCALE, ALTURA * SCALE));
		setFocusable(true);
		requestFocus();
	}
	
	public void addNotify(){
		super.addNotify();
		if(thread == null){
			thread = new Thread(this);
			addKeyListener(this);
			thread.start();
		}
	}
	
	private void init(){
		image = new BufferedImage(LARGURA, ALTURA, BufferedImage.TYPE_INT_RGB);
		g = (Graphics2D) image.getGraphics();
		rodando  = true;
		
		gsm = new GameStateManager();
	}

	@Override
	public void run() {
		init();

		while(rodando){
			
			long inicio;
			long decorrido;
			long espera;
			
			inicio = System.nanoTime();
			
			update();
			draw();
			drawToScreen();
			
			decorrido = System.nanoTime() - inicio;
			
			espera = targetTime  - decorrido / 1000000;
			if(espera < 0) espera = 5;
			try {
				Thread.sleep(espera);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	private void update(){
		gsm.update();
		KeyHandler.update();
		KeyHandler2.update();
	}
	
	private void draw(){
		gsm.draw(g);
	}
	private void drawToScreen(){
		Graphics g2 = getGraphics();
		g2.drawImage(image, 0, 0, LARGURA * SCALE, ALTURA * SCALE, null);
		g2.dispose();
	}

	@Override
	public void keyPressed(KeyEvent key) {
		KeyHandler.keySet(key.getKeyCode(), true);
		KeyHandler2.keySet(key.getKeyCode(), true);
	}

	@Override
	public void keyReleased(KeyEvent key) {
		KeyHandler.keySet(key.getKeyCode(), false);
		KeyHandler2.keySet(key.getKeyCode(), false);
	}

	@Override
	public void keyTyped(KeyEvent key) {}

}
