//package tokens;

public class Token{
	protected int row, col;
	protected String input;
	protected String name;
	protected int val;

	public Token(String i, int r, int c){
	 	this.input = i;
	 	this.row = r;
	 	this.col = c;
	 	name = "";
	}

	public Token(int r, int c){
	 	this.row = r;
	 	this.col = c;
	 	name = "";
	}
	public String getVarName() {
		return "";
	}

	public int getVal(){ return val; }

	public void setVal(int v) { val = v; }

	public int getRow(){ return row; }

	public int getCol(){ return col; }

	public String getTokenType(){ return input; }

	public void setName(String n){ name = n; }

	public String getName(){ return name; }

	@Override
	public String toString(){
	 	return "[" + row + ", " + col + "]";
	}
}