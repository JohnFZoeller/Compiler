//package tokens;

public class Keyword extends Token{
	public Keyword(String input, String output, int r, int c){
		super(output, r, c);
	}

	@Override
	public String toString(){
		return super.toString() + " " + this.input;
	}
}