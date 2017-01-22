package go;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GameFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	public GameFrame() throws IOException {
		super("Go");

		JPanel obrazPanel = new GamePanel(null, null);
		add(obrazPanel);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
}