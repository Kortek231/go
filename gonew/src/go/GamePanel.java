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
	private char mark;
	public GamePanel(Socket socket, Goban board, char mark) throws IOException {
		super();
		this.socket=socket;
		this.board=board;
		this.mark=mark;
		System.out.println(mark);
      //  BufferedReader in = new BufferedReader(new InputStreamReader(
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
					
				/*if(board.intersections[i][j].state == FieldState.free){
						board.intersections[i][j].state = col;
						}*/
						if(!finish){
							if(Goban.intersections[i][j].state == FieldState.free){
								Goban.intersections[i][j].state = col;
								List<Field> neighbours = new ArrayList<Field>();
								neighbours = Goban.neighbourhood(Goban.intersections[i][j]);
								boolean neigh_death = false;
								for(Field f : neighbours){
									if (!Goban.checkalive(f.x, f.y)){
										neigh_death = true;
									}
									Goban.chain.clear();
								}
								if(neigh_death){	
									Goban.checkalive(i,j);
									for(Field f : Goban.chain){
										f.immortality = true;
									}
								}
								if(last_death_size == 1 && last_death == Goban.intersections[i][j]) ko = true;
								
								if((Goban.checkalive(i,j) || neigh_death) && !ko ){
									Goban.chain.clear();
									if(col == FieldState.black){
										col = FieldState.white;
										PunctationFrame.label_mv.setText("White player move");
									}
									else if(col == FieldState.white){
										col = FieldState.black;						
										PunctationFrame.label_mv.setText("Black player move");
									}
			
									repaint();
									
									for(int k = 0 ; k <= 18 ; k++){
										for(int p = 0 ; p <= 18 ; p++){
											if(Goban.intersections[k][p].state != FieldState.free)
												if(!Goban.checkalive(k,p) && !Goban.intersections[k][p].immortality){
													last_death = Goban.intersections[k][p];
													last_death_size = Goban.chain.size();
													System.out.println(last_death_size);
													if(Goban.intersections[k][p].state == FieldState.black){
														pkt_w += Goban.chain.size();
														//Goban.point_w = pkt_w;
														PunctationFrame.label_w.setText("White player points: " + Integer.toString(pkt_w));
													}
													if(Goban.intersections[k][p].state == FieldState.white){
														pkt_b += Goban.chain.size();
														//Goban.point_b = pkt_b;
														PunctationFrame.label_b.setText("Black player points: " + Integer.toString(pkt_b));
													}
													for(Field field : Goban.chain){
														field.uncover();
													}
												}else{
													Goban.chain.clear();
													for(int q = 0 ; q <=18 ; q++){
														for(int u = 0 ; u <=18 ; u++){
															Goban.intersections[k][p].immortality = false;
														}
													}
												}
										}
									}
								}else{
									Goban.intersections[i][j].uncover();
									if(ko){
										ko = false;
										last_death.x = -1;
									}
								}
							}
							} else {
								if(agree)
									if(Goban.territory[i][j].state == FieldState.free){
										Goban.territory[i][j].state = col;
										if(col == FieldState.black) col = FieldState.white;
										else col = FieldState.black;
										PunctationFrame.label_mv.setText("Does " + col + " player agree?");
										agree = false;
										
										repaint();
							}
					
				}
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
				if(board.intersections[i][j].state == FieldState.black){
					g2d.drawImage(black, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(board.intersections[i][j].state == FieldState.white){
					g2d.drawImage(white, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(board.territory[i][j].state == FieldState.black){
					g2d.drawImage(blackmark, (i+1)*30-10, (j+1)*30-10, this);
				}
				if(board.territory[i][j].state == FieldState.white){
					g2d.drawImage(whitemark, (i+1)*30-10, (j+1)*30-10, this);
				}
			}
		}
	}
}
