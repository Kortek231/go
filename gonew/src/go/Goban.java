package go;
import java.util.*;

public class Goban {
	
	static Field[][] intersections = new Field[19][19];
	static Field[][] territory = new Field[19][19];
	static List<Field> chain = new ArrayList<Field>();
	//static int point_b,point_w;
	Goban(){
		for(int i = 0 ; i <= 18 ; i++){
			for(int j = 0 ; j <= 18 ; j++){
				intersections[i][j] = new Field();
				intersections[i][j].x = i;
				intersections[i][j].y = j;
				intersections[i][j].immortality = false;
				territory[i][j] = new Field();
				territory[i][j].x = i;
				territory[i][j].y = j;
			}
		}
	}
	
	static Field getfield(int x, int y){
		return intersections[x][y];
	}
	
	static int countbreaths(int x, int y){
		int ret = 0;
		if(x >= 1)
			if(intersections[x-1][y].state == FieldState.free){
				ret++;
			}
		if(x <= 17)
			if(intersections[x+1][y].state == FieldState.free){
				ret++;
			}
		if(y >= 1)
			if(intersections[x][y-1].state == FieldState.free){
				ret++;
			}
		if(y <= 17)
			if(intersections[x][y+1].state == FieldState.free){
				ret++;
			}
		return ret;
	}
	
	/*List chaincreator(int x, int y){
		List<Field> chain = new ArrayList<Field>();
	}*/
	
	static boolean checkalive(int x, int y){
		//int ret = 0;
		//if(countbreaths(x,y) >= 1) ret = 1;
		//else if(ret != 1) ret = 0;
		boolean ch;
		chain.add(intersections[x][y]);
		if(countbreaths(x,y) > 0) return true;
	
		if(x >= 1)
		if(intersections[x-1][y].state == intersections[x][y].state){
			ch = true;
			for(Field field : chain){
				if(field.x == x-1 && field.y == y) ch = false;
			}
			if(ch) checkalive(x-1,y);
		}
		if(x <= 17)
		if(intersections[x+1][y].state == intersections[x][y].state){
			ch = true;
			for(Field field : chain){
				if(field.x == x+1 && field.y == y) ch = false;
			}
			if(ch) checkalive(x+1,y);
		}
		if(y >= 1)
		if(intersections[x][y-1].state == intersections[x][y].state){
			ch = true;
			for(Field field : chain){
				if(field.x == x && field.y == y-1) ch = false;
			}
			if(ch) checkalive(x,y-1);
		}
		if(y <= 17)
		if(intersections[x][y+1].state == intersections[x][y].state){
			ch = true;
			for(Field field : chain){
				if(field.x == x && field.y == y+1) ch = false;
			}
			if(ch) checkalive(x,y+1);
		}
		boolean check = false;
		
		for(Field field : chain){
			if(countbreaths(field.x , field.y) > 0) check = true;
		}
		if(check) return true;
		else return false;
	}
	
	static List<Field> neighbourhood(Field f){
		List<Field> neighbours = new ArrayList<Field>();
		if(f.x >= 1)
		if(intersections[f.x-1][f.y].state != f.state && intersections[f.x-1][f.y].state != FieldState.free)
		neighbours.add(intersections[f.x-1][f.y]);
		if(f.x <= 17)
		if(intersections[f.x+1][f.y].state != f.state && intersections[f.x+1][f.y].state != FieldState.free)
		neighbours.add(intersections[f.x+1][f.y]);
		if(f.y >= 1)
		if(intersections[f.x][f.y-1].state != f.state && intersections[f.x][f.y-1].state != FieldState.free)
		neighbours.add(intersections[f.x][f.y-1]);
		if(f.y <= 17)
		if(intersections[f.x][f.y+1].state != f.state && intersections[f.x][f.y+1].state != FieldState.free)
		neighbours.add(intersections[f.x][f.y+1]);
		return neighbours;
	}
}
