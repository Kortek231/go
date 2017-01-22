package go;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.event.*;
//import java.awt.geom.Point2D;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel {
	private static final long serialVersionUID = 6798557487676021660L;
	public static int pkt_b = 0,pkt_w = 0;
	private BufferedImage image;
	private BufferedImage white;
	private BufferedImage black;
	private BufferedImage blackmark;
	private BufferedImage whitemark;
	static FieldState col;
	static boolean finish, pass, agree = true;
	static FieldState winner;
	private Socket socket;
	private Goban board;
	char mark;
	public GamePanel(Socket socket, Goban board) throws IOException {
		super();
		repaint();
		System.out.println(socket);
		this.socket=socket;
		this.board=board;
		this.mark=mark;
       // BufferedReader in = new BufferedReader(new InputStreamReader(
         //       socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		addMouseListener(new MouseAdapter()
		{
			int x,y,i = 0,j = 0;
			Field last_death = new Field();
			int last_death_size = 0;
			boolean ko = false;
			@Override
			
			public void mousePressed(MouseEvent e) 
			{
				//System.out.println("klik");
				if(SwingUtilities.isLeftMouseButton(e))
				{
					repaint();
					x = e.getX();
					y = e.getY();
					
					boolean end = false;
					for(int k = 1 ; k <= 19 ; k++){
						if((30 * k - 15) <= x && (30 * k + 15) > x){
							i = k-1;
							end = true;
						}
					}
					if(end == false) i = 0;
					end = false;
					for(int k = 1 ; k <= 19 ; k++){
						if((30 * k - 15) <= y && (30 * k + 15) > y){
							j = k-1;
							end = true;
						}
					}
					if(end == false) j = 0;
					end = false;
					if (mark== 'B') col = FieldState.black;
					else col = FieldState.white;
					out.println("MOVE "+i+" "+j);
									
					}
				}
			public void mouseReleased(MouseEvent e){
				repaint();
			}
			}
			);

		File imageFile = new File("goban.jpg");
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			System.err.println("Blad odczytu obrazka");
			e.printStackTrace();
		}
		File whiteFile = new File("white.jpg");
		try {
			white = ImageIO.read(whiteFile);
		} catch (IOException e) {
			System.err.println("Blad odczytu obrazka");
			e.printStackTrace();
		}
		File blackFile = new File("black.jpg");
		try {
			black = ImageIO.read(blackFile);
		} catch (IOException e) {
			System.err.println("Blad odczytu obrazka");
			e.printStackTrace();
		}
		File blackmarkFile = new File("blackmark.jpg");
		try {
			blackmark = ImageIO.read(blackmarkFile);
		} catch (IOException e) {
			System.err.println("Blad odczytu obrazka");
			e.printStackTrace();
		}
		File whitemarkFile = new File("whitemark.jpg");
		try {
			whitemark = ImageIO.read(whitemarkFile);
		} catch (IOException e) {
			System.err.println("Blad odczytu obrazka");
			e.printStackTrace();
		}

	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.drawImage(image, 0, 0, this);
		
		for(int i = 0 ; i <= 18 ; i++){
			for(int j = 0 ; j <= 18 ; j++){
				if(Goban.intersections[i][j].state == FieldState.black){
					g2d.drawImage(black, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(Goban.intersections[i][j].state == FieldState.white){
					g2d.drawImage(white, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(Goban.territory[i][j].state == FieldState.black){
					g2d.drawImage(blackmark, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(Goban.territory[i][j].state == FieldState.white){
					g2d.drawImage(whitemark, (i+1)*30-10, (j+1)*30-10, this);
				}
			}
		}
	}
}
