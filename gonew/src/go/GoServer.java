package go;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class GoServer {

	//uruchamia aktywacje i laczy klientow
	
	public static void main(String[] args) throws Exception {
		ServerSocket listener = new ServerSocket(8901);
		System.out.println("Go server is running");
		try{
			while(true){
				Game game = new Game();
				Game.Player playerB = game.new Player(listener.accept(), 'B');
				Game.Player playerW = game.new Player(listener.accept(), 'W');
				playerB.setOpponent(playerW);
				playerW.setOpponent(playerB);
				game.currentPlayer=playerB;
				playerB.start();
				playerW.start();
			}
		}finally{
			listener.close();
		}
	}
}

class Game {
	
	static FieldState col = FieldState.black;
	static boolean finish, pass, agree = true;
	static FieldState winner;
	int x,y,i = 0,j = 0;
	Field last_death = new Field();
	int last_death_size = 0;
	boolean ko = false;
	public static int pkt_b = 0,pkt_w = 0;
	
	//plansza do gry, kazde pole ma swoj stan - bialy, czarny, brak
	public Goban board = new Goban();

	Player currentPlayer;
	
	//sprawdza czy jest zwyciezca
	
	//sprawdza czy mozna wykonac ruch
	public synchronized boolean legalMove(int x, int y, Player player){
		if(player == currentPlayer && board.intersections[x][y].state == FieldState.free
				&& board.countbreaths(x, y)!=0){
			if(currentPlayer.mark=='B') { 
				board.intersections[x][y].state = FieldState.black;
			} else board.intersections[x][y].state = FieldState.white;
			

			board.intersections[x][y].state = col;
			List<Field> neighbours = new ArrayList<Field>();
			neighbours = board.neighbourhood(board.intersections[x][y]);
			boolean neigh_death = false;
			for(Field f : neighbours){
				if (!board.checkalive(f.x, f.y)){
					neigh_death = true;
				}
				board.chain.clear();
			}
			if(neigh_death){	
				board.checkalive(i,j);
				for(Field f : board.chain){
					f.immortality = true;
				}
			}
			if(last_death_size == 1 && last_death == board.intersections[x][y]) ko = true;
			
			if((board.checkalive(x,y) || neigh_death) && !ko ){
				board.chain.clear();
				if(col == FieldState.black){
					col = FieldState.white;
					//PunctationFrame.label_mv.setText("White player move");
				}
				else if(col == FieldState.white){
					col = FieldState.black;						
				//	PunctationFrame.label_mv.setText("Black player move");
				}
				
				for(int k = 0 ; k <= 18 ; k++){
					for(int p = 0 ; p <= 18 ; p++){
						if(board.intersections[k][p].state != FieldState.free)
							if(!board.checkalive(k,p) && !board.intersections[k][p].immortality){
								last_death = board.intersections[k][p];
								last_death_size = board.chain.size();
								System.out.println(last_death_size);
								if(board.intersections[k][p].state == FieldState.black){
									pkt_w += board.chain.size();
									//Goban.point_w = pkt_w;
									//PunctationFrame.label_w.setText("White player points: " + Integer.toString(pkt_w));
								}
								if(board.intersections[k][p].state == FieldState.white){
									pkt_b += board.chain.size();
									//Goban.point_b = pkt_b;
									//PunctationFrame.label_b.setText("Black player points: " + Integer.toString(pkt_b));
								}
								for(Field field : board.chain){
									field.uncover();
								}
							}else{
								board.chain.clear();
								for(int q = 0 ; q <=18 ; q++){
									for(int u = 0 ; u <=18 ; u++){
										board.intersections[k][p].immortality = false;
									}
								}
							}
					}
				}
			}else{
				board.intersections[x][y].uncover();
				if(ko){
					ko = false;
					last_death.x = -1;
				}
			}
		
			
			currentPlayer = currentPlayer.opponent;
			currentPlayer.otherPlayerMoved(x, y);
			return true;
			}
		return false;
		} 	
	
	/*public synchronized boolean legalMove(int x, int y, Player player){
		if(currentPlayer.mark=='B') { 
			board.intersections[x][y].state = FieldState.black;
		} else board.intersections[x][y].state = FieldState.white;
		currentPlayer = currentPlayer.opponent;
		currentPlayer.otherPlayerMoved(x, y);
		return true;
	}*/
	
	class Player extends Thread {
		char mark;
		Player opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;
		
		public Player(Socket socket, char mark){
			this.socket = socket;
			this.mark = mark;
			try{
				input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("WELCOME " + mark);
				output.println("MESSAGE Waiting for opponent to connect");
			} catch (IOException e){
				System.out.println("Player disconnected: "+e);
			}
		}
		
		public void setOpponent(Player opponent){
			this.opponent=opponent;
		}
		
		public void otherPlayerMoved(int x, int y) {
			output.println("OPPONENT_MOVED "+x+" "+y);
		}
		
		public void run(){
            try {
            	// tylko jesli wszyscy sie polacza
                output.println("MESSAGE All players connected");

                // Mowi graczowi, ze jego tura
                if (mark == 'B') {
                    output.println("MESSAGE Your move");
                }
                
                //pobiera polecenia i przetwarza je
                while (true){
                	String command = input.readLine();
                	if (command.startsWith("MOVE")) {
                		//System.out.println(command);     
                		if(command.length()==8){
                		x = Integer.parseInt(command.substring(5,6));
                		y = Integer.parseInt(command.substring(7,8));
                		}else if(command.length()==9){
                			if(command.charAt(7)==' '){
                        		x = Integer.parseInt(command.substring(5,7));
                        		y = Integer.parseInt(command.substring(8,9));
                			} else{
                				x = Integer.parseInt(command.substring(5,6));
                        		y = Integer.parseInt(command.substring(7,9));
                			  }
                		}else if(command.length()==10){
            				x = Integer.parseInt(command.substring(5,7));
                    		y = Integer.parseInt(command.substring(8,10));
            			  }
                		if (legalMove(x, y, this)) {
                			output.println("VALID_MOVE "+x+" "+y);
                		} else {
                			output.println("MESSAGE Invalid move");
                		}
                	} else if(command.startsWith("FINISH")){
                		if (command.endsWith("BLACK")){
						output.println("FINISH BLACK");
					} else if (command.endsWith("WHITE")){
						output.println("FINISH WHITE");
					} 
                		
                	} else if (command.startsWith("PASS")){
                		if (command.endsWith("BLACK")) {
                			output.println("PASS BLACK");
                			currentPlayer = currentPlayer.opponent;
                		} else if (command.endsWith("PASS WHITE")){
                			output.println("PASS WHITE");
                			currentPlayer = currentPlayer.opponent;
                		}
                	}
           
                	else if (command.startsWith("QUIT")){
                		return;
                	}
                }
			} catch (IOException e) {
                System.out.println("Player disconnected: " + e);
            } finally {
                try {socket.close();} catch (IOException e) {}
            }
	}
}
}
