package view;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import controller.ControlTelas;
import model.states.GameStateManager;
import model.states.ScoreMenuState;

@SuppressWarnings("serial")
public class SetPlayerName extends JFrame implements KeyListener{
	
	public static JTextField txtNome;
	JPanel panel = new JPanel();
	JLabel label = new JLabel("Digite seu nome: ");
	
	public SetPlayerName(){
		setUndecorated(true);
		setLayout(new FlowLayout());
		txtNome = new JTextField(8);
		label.setForeground(Color.WHITE);
		panel.setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
		panel.add(label);
		panel.add(txtNome);
		txtNome.setDocument(new JTextFieldLimit(10));
		txtNome.addKeyListener(this);
		txtNome.requestFocusInWindow();
		getContentPane().add("Center", panel);
		setResizable(false);
		setLocation(380, 180);
		setBackground(new Color(1.0f,1.0f,1.0f,0.0f));
		setSize(280, 30);
		setVisible(true);
		
		ControlTelas.disableFocus();
	
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER){
			dispose();
			ControlTelas.enableFocus();
			ScoreMenuState.Salvar();
			ScoreMenuState.update = true;
		}
		
	}
	
	class JTextFieldLimit extends PlainDocument {
		  private int limit;
		  JTextFieldLimit(int limit) {
		    super();
		    this.limit = limit;
		  }

		  JTextFieldLimit(int limit, boolean upper) {
		    super();
		    this.limit = limit;
		  }

		  public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
		    if (str == null)
		      return;

		    if ((getLength() + str.length()) <= limit) {
		      super.insertString(offset, str, attr);
		    }
		  }
		}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}


}
