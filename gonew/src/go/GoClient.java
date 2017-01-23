package go;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.*;
import java.awt.*;

public class GoClient implements ImageObserver{

    private JFrame frame = new JFrame();
    private JLabel messageLabel = new JLabel("");
    private FieldState icon;
    private FieldState opponentIcon;
	
    
    private Goban board = new Goban();
	private BufferedImage image;
	private BufferedImage whiteW;
	private BufferedImage blackB;
	private BufferedImage whitemark;
	private BufferedImage blackmark;
	private static int PORT = 8901;
	private Socket socket;
	private BufferedReader input;
	private PrintWriter output;
	char mark;
	int locx, locy;
	
	public GoClient(String serverAddress) throws Exception{
		//network
		socket = new Socket(serverAddress, PORT);
		input = new BufferedReader
			(new InputStreamReader(socket.getInputStream()));
		output = new PrintWriter(socket.getOutputStream(), true);
		//Layout GUI
		frame.getContentPane().add(messageLabel, "South");
		JPanel obrazPanel = new GamePanel(socket, board);
		frame.getContentPane().add(obrazPanel, "Center");

	}
	
	
	//odbior polecen
	public void play() throws Exception {
		String response;
		try {
			response = input.readLine();
			if (response.startsWith("WELCOME")) {
				mark = response.charAt(8);
				if (mark == 'B') icon = FieldState.black;
				else icon = FieldState.white;
				if (mark == 'W') opponentIcon = FieldState.black;
				else opponentIcon = FieldState.white;
				frame.setTitle("GO player " + mark);
			}
			while (true){
				response = input.readLine();
				System.out.println(response);
			
				if (response.startsWith("VALID_MOVE")) {
					if(response.length()==14){
					locx = Integer.parseInt(response.substring(11,12));
					locy = Integer.parseInt(response.substring(13,14));
					} else if(response.length()==15){
						if(response.charAt(13)==' '){
						locx = Integer.parseInt(response.substring(11,13));
						locy = Integer.parseInt(response.substring(14,15));
						} else {
							locx = Integer.parseInt(response.substring(11,12));
							locy = Integer.parseInt(response.substring(13,15));
							}
					} else if(response.length()==16){
						locx = Integer.parseInt(response.substring(11,13));
						locy = Integer.parseInt(response.substring(14,16));
					}
					board.intersections[locx][locy].state=icon;
					if (response.endsWith("CLEAR")) board.chain.clear();
					messageLabel.setText("Please wait for your opponent move");
					frame.getContentPane().validate();
			        frame.getContentPane().repaint();
				} else if (response.startsWith("OPPONENT_MOVED")) {
					System.out.println("dlug "+response.length());
					if(response.length()==18){
					locx = Integer.parseInt(response.substring(15,16));
					locy = Integer.parseInt(response.substring(17,18));
					} else if(response.length()==19){
						if(response.charAt(17)==' '){
						locx = Integer.parseInt(response.substring(15,17));
						locy = Integer.parseInt(response.substring(18,19));
						} else {
							locx = Integer.parseInt(response.substring(15,16));
							locy = Integer.parseInt(response.substring(17,19));
							}
					} else if(response.length()==20){
						locx = Integer.parseInt(response.substring(15,17));
						locy = Integer.parseInt(response.substring(18,20));
					}
					board.intersections[locx][locy].state=opponentIcon;
					messageLabel.setText("Opponent moved, now it's your turn");
					frame.getContentPane().validate();
			        frame.getContentPane().repaint();
				} else if (response.startsWith("VICTORY")) {
					messageLabel.setText("You win");
					break;
				} else if (response.startsWith("DEFEAT")) {
					messageLabel.setText("You lose");
				} else if (response.startsWith("MESSAGE")) {
					messageLabel.setText(response.substring(8));
				}
			}
			output.println("QUIT");
		}finally {
			socket.close();
		}
	}
	
	//jeszcze raz?
	private boolean wantsToPlayAgain() {
		int response = JOptionPane.showConfirmDialog(frame, 
				"Do you want to play again?", 
				"Let's have more fun!", 
				JOptionPane.YES_NO_OPTION);
		return response == JOptionPane.YES_OPTION;
	}
	
	public static void main(String[] args) throws Exception {
		while (true) {
			String serverAddress = (args.length == 0) ? "localhost" : args[1];
			GoClient client = new GoClient(serverAddress);
			//ustawienia okna
			client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			client.frame.setSize(650, 650);
			client.frame.setVisible(true);
			client.frame.setResizable(true);
			client.play();
			if (!client.wantsToPlayAgain()){
				break;
			}
		}
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}
}
