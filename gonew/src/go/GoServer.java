package go;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

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
	
	//plansza do gry, kazde pole ma swoj stan - bialy, czarny, brak
	public Goban board = new Goban();

	Player currentPlayer;
	
	//sprawdza czy jest zwyciezca
	public boolean hasWinner() {
		return true;
	
	}
	
	//sprawdza czy mozna wykonac ruch
	public synchronized boolean legalMove(int x, int y, Player player){
		if(player == currentPlayer && board.intersections[x][y].state == FieldState.free
				&& Goban.countbreaths(x, y)!=0){
			if(currentPlayer.mark=='B') { 
				board.intersections[x][y].state = FieldState.black;
			} else board.intersections[x][y].state = FieldState.white;
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
			output.println("OPPONENT_MOVED "+x+y);
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
                			System.out.println(command);  
                			output.println("VALID_MOVE "+x+y);
                			System.out.println(command);  
                		} else {
                			output.println("MESSAGE Invalid move");
                		}
                	} else if (command.startsWith("QUIT")){
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
