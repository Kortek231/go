package go;

import static org.junit.Assert.*;

import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.List;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import go.Game.Player;

public class GobanTest {
	
	Goban board;
	@Before
	public void setUp() throws Exception {
		board = new Goban();
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testBoard(){
		for(int i = 0 ; i <= 18 ; i++){
			for(int j = 0 ; j <= 18 ; j++){
				assertEquals(board.intersections[i][j].state, FieldState.free);
			}
		}
	}
	@Test
	public void testInput() {
		assertEquals(board.intersections[1][1].state, FieldState.free);
		board.intersections[1][1].state=FieldState.black;
		assertNotEquals(board.intersections[1][1].state, FieldState.free);
	}
	
	@Test
	public void testSuicide(){
		board.intersections[1][0].state=FieldState.black;
		board.intersections[0][1].state=FieldState.black;
		assertEquals(false, (board.countbreaths(1, 1)==0));
		board.intersections[2][1].state=FieldState.black;
		board.intersections[1][2].state=FieldState.black;
		assertEquals(true, (board.countbreaths(1, 1)==0));
		
		assertEquals(4, board.countbreaths(5, 5));
	}
	
	@Test
	public void testAlive(){
		board.intersections[1][0].state=FieldState.black;
		assertEquals(true, (board.checkalive(1, 1)));
		board.intersections[0][1].state=FieldState.black;
		assertEquals(true, (board.checkalive(1, 1)));
		board.intersections[2][1].state=FieldState.black;
		assertEquals(true, (board.checkalive(1, 1)));
		board.intersections[1][2].state=FieldState.black;
		assertEquals(false, (board.checkalive(1, 1)));
		
		board.intersections[10][0].state=FieldState.black;
		assertEquals(true, (board.checkalive(11, 11)));
		board.intersections[10][1].state=FieldState.black;
		assertEquals(true, (board.checkalive(11, 11)));
		board.intersections[12][1].state=FieldState.black;
		assertEquals(true, (board.checkalive(11, 11)));
		board.intersections[11][2].state=FieldState.black;
		assertEquals(true, (board.checkalive(11, 11)));
		

		board.intersections[2][2].state=FieldState.black;
		board.intersections[3][2].state=FieldState.white;
		board.intersections[2][3].state=FieldState.black;
		board.intersections[0][2].state=FieldState.white;
		board.intersections[3][0].state=FieldState.white;
		board.intersections[0][3].state=FieldState.black;
		assertEquals(true, (board.checkalive(2, 2)));
		assertEquals(true, (board.checkalive(3, 2)));
		assertTrue(board.checkalive(2, 1));
		assertSame((board.checkalive(1, 2)), (board.checkalive(2, 2)));

		board.intersections[10][2].state=FieldState.black;
		board.intersections[11][3].state=FieldState.black;
		board.intersections[11][1].state=FieldState.black;
		board.intersections[12][2].state=FieldState.black;
		assertTrue(board.checkalive(11, 2));
		board.intersections[11][2].state=FieldState.white;
		assertSame((board.checkalive(10, 2)), (board.checkalive(12, 2)));
	}
	
	@Test
	public void testGetField(){
		assertEquals(board.intersections[2][2], board.getfield(2, 2));
		assertNotEquals(board.intersections[5][2], board.getfield(2, 2));
	}

	@Test
	public void testCover(){
		FieldState col = FieldState.black;
		board.intersections[2][2].cover(col);
		assertEquals(board.intersections[2][2].state,FieldState.black);

		FieldState col2 = FieldState.white;
		board.intersections[4][2].cover(col2);
		assertNotEquals(board.intersections[4][2].state,FieldState.black);
		assertNotEquals(board.intersections[4][2].state,FieldState.free);
		assertEquals(board.intersections[4][2].state,FieldState.white);
	}
	
	@Test
	public void testUncover(){
		board.intersections[4][2].state=FieldState.black;
		assertEquals(board.intersections[4][2].state,FieldState.black);
		board.intersections[4][2].uncover();
		assertNotEquals(board.intersections[4][2].state,FieldState.black);
		assertEquals(board.intersections[4][2].state,FieldState.free);
	}
	
	@Test
	public void testCase() throws IOException{
		assertEquals(true, GamePanel.agree);
		assertEquals(false, GamePanel.pass);
		assertEquals(false, GamePanel.finish);		
		File imageFile = new File("goban.jpg");
			BufferedImage image = ImageIO.read(imageFile);
	}

	@Test
	public void testLoadImage() throws IOException{
		File imageFile = new File("goban.jpg");
		BufferedImage image = ImageIO.read(imageFile);	
		assertNotEquals(0,image);
	}
	
	@Test
	public void testNeighbours(){
		board.intersections[1][1].state=FieldState.black;
		board.intersections[2][1].state=FieldState.white;
		board.intersections[3][1].state=FieldState.black;
		board.intersections[3][0].state=FieldState.white;
		board.intersections[1][3].state=FieldState.black;
		board.intersections[3][2].state=FieldState.black;
		board.intersections[0][3].state=FieldState.white;
		board.intersections[2][3].state=FieldState.black;
		board.intersections[0][1].state=FieldState.black;
		board.intersections[1][2].state=FieldState.black;
		board.intersections[2][2].state=FieldState.black;
		board.intersections[0][2].state=FieldState.black;
		board.intersections[1][0].state=FieldState.black;
		board.intersections[0][0].state=FieldState.black;
		Field f = board.intersections[2][1];
		assertNotEquals(null, board.neighbourhood(f));
	}

	@Test
	public void testButtons(){
		JLabel label_w = new JLabel();
		JLabel label_b = new JLabel("Black player points: 0");
		JLabel label_f = new JLabel("");
		JButton button_pass = new JButton("Pass");
		JButton button_concede = new JButton("Concede");
		assertSame("", label_w.getText());
		label_w.setText("White player points: 0");
		assertNotSame(label_w,label_b);
		assertNotEquals(label_w, null);
		assertNotEquals(label_b, null);
		assertNotEquals(label_f, null);
		assertNotEquals(button_pass, null);
	}
	
	@Test
	public void testMark(){
		char mark = 'W';
		JLabel label_mv = new JLabel();
		assertNotNull(mark);

		if (mark == 'W') label_mv = new JLabel("White player move");
		if (mark == 'B') label_mv = new JLabel("Black player move");
		assertFalse(("Black player move")== label_mv.getText());
		assertTrue(("White player move")== label_mv.getText());
		mark = 'B';
		if (mark == 'W') label_mv = new JLabel("White player move");
		if (mark == 'B') label_mv = new JLabel("Black player move");
		assertTrue(("Black player move")== label_mv.getText());
		assertFalse(("White player move")== label_mv.getText());
		
	}
	
	@Test
	public void testPackage(){
		Package pack = FieldState.class.getPackage();
		Package pack2 = Goban.class.getPackage();
		assertEquals(pack, FieldState.class.getPackage());
		assertNotNull(FieldState.class.getPackage());
		assertSame(pack, pack2);
		
	}
	
	@Test
	public void testGamePanel() throws IOException{
		int x = 0,y = 0,i = 0,j = 0;
		Field last_death = new Field();
		int last_death_size = 0;
		boolean ko = false;
		assertTrue(x==0 || y==0 || i==0 || j == 0);
	}
	
	@Test(expected=NullPointerException.class)
	public void testPlayer() throws Exception{
		Game game = new Game();
		Game.Player playerB = game.new Player(null, 'B');
		
	}
	
	@Test
	public void testField() throws IOException{
		assertNotEquals(GamePanel.col, FieldState.free);
	}
	
	
	
}
