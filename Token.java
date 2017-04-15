//package tokens;

public class Token{
	protected int row, col;
	protected String input;

	public int getRow(){
		return row;
	}

	public int getCol(){
		return col;
	}

	public Token(String i, int r, int c){
	 	this.input = i;
	 	this.row = r;
	 	this.col = c;
	}

	public Token(int r, int c){
	 	this.row = r;
	 	this.col = c;
	}


	 @Override
	 public String toString(){
	 	return "[" + row + ", " + col + "]";
	 }
}