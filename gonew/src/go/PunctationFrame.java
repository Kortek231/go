package go;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.event.*;

public class PunctationFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	static JLabel label_w = new JLabel("White player points: 0");
	static JLabel label_b = new JLabel("Black player points: 0");
	static JLabel label_f = new JLabel("");
	static JLabel label_mv = new JLabel("Black player move");
	static JButton button_pass = new JButton("Pass");
	static JButton button_concede = new JButton("Concede");
	
	
	public PunctationFrame() {
		super("Punctation");
		this.setSize(300, 350);
		this.setLayout(null);
		
		label_w.setSize(200, 20);
		label_w.setLocation(10,30);
		add(label_w);
		
		label_b.setSize(200, 20);
		label_b.setLocation(10,120);
		add(label_b);
		
		label_mv.setSize(200, 20);
		label_mv.setLocation(85, 160);
		add(label_mv);
		
		label_f.setSize(400, 20);
		label_f.setLocation(75, 250);
		add(label_f);
		
		button_concede.setSize(100, 20);
		button_concede.setLocation(150, 200);
		button_concede.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(GamePanel.col == FieldState.black){
					GamePanel.winner = FieldState.white;
					label_f.setText("And the winner is... BLACK");
				}
				else{
					GamePanel.winner = FieldState.black;
					label_f.setText("And the winner is... WHITE");
				}
				GamePanel.finish = true;
			}
		});
		add(button_concede);
		
		button_pass.setSize(100, 20);
		button_pass.setLocation(30, 200);
		button_pass.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(button_pass.getText() == "Pass"){
					if(!GamePanel.pass){
						if(GamePanel.col == FieldState.black) GamePanel.col = FieldState.white;
						else GamePanel.col = FieldState.black;
						GamePanel.pass = true;
					}else{
						GamePanel.finish = true;
						label_f.setText("Final phase of the game");
						button_pass.setText("Agree");
					}
				}else{
					GamePanel.agree = true;
					label_mv.setText("Player " + GamePanel.col + " move");
				}
			}	
		});
		add(button_pass);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//pack();
		setVisible(true);
	}
}