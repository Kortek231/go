package go;

import static org.junit.Assert.*;

import java.awt.List;
import java.awt.Point;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import go.Game.Player;

public class GobanTest {
	
	Goban board;
	//Game game = new Game();
	//Game.Player playerW = game.new Player(null, 'W');
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
	public void testMark() {
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
	}
	
	@Test
	public void testPlayer(){
		
	}
	
	
	

}
