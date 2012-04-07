package pentagoxl.client;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class GameOverGUI extends JFrame {

	private static final long serialVersionUID = -6547541046379970274L;

	/**
	 * Constructs a new GameOverGUI and adds a JTextArea with <tt>winnaars</tt> in it. Title is based on <tt>gewonnen</tt>
	 * @param winnaars
	 * @param gewonnen
	 */
	public GameOverGUI(String[] winnaars, boolean gewonnen) {
		//String title = gewonnen ? "Contgrats!" : "Fail!";
		super(gewonnen ? "Contgrats!" : "Fail!");
		
		JTextArea jta = new JTextArea();
		String text = "Winnaars:";
		for (String s : winnaars) {
			text += "\n" + s;
		}
		jta.setText(text);
		this.getContentPane().add(jta);
		
		this.pack();
		
		this.setVisible(true);
	}
}
