package go;

import java.awt.EventQueue;
import java.io.IOException;

public class Test {
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Goban table = new Goban();
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					new GameFrame();
				} catch (IOException e) {
					e.printStackTrace();
				}
				new PunctationFrame();
			}
		});
	}
}