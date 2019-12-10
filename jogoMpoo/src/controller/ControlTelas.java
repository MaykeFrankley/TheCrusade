package controller;

import javax.swing.JFrame;

import view.JogoPanel;

public class ControlTelas {
	
	static JFrame janela = new JFrame("The Crusade");
	
	public ControlTelas(){
		janela.setUndecorated(true);
		janela.add(new JogoPanel());
		janela.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		janela.setResizable(false);
		janela.pack();
		janela.setLocationRelativeTo(null);
		janela.setVisible(true);
	}
	
	public static void disableFocus(){
		janela.setFocusableWindowState(false);
	}
	
	public static void enableFocus(){
		janela.setFocusableWindowState(true);
	}

}
