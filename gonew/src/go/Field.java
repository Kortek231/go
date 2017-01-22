package go;

public class Field {
	
	FieldState state = FieldState.free;
	int x,y;
	boolean immortality;
	
	void cover(FieldState col){
		if(this.state == FieldState.free){
			this.state = col;
		}
	}
	
	void uncover(){
		this.state = FieldState.free;
	}
	
	
	
}
