//package tokens;

public class Keyword extends Token{
	String input;

	public Keyword(String in, String out, int r, int c){
		super(out, r, c);
		this.input = in;
	}

	@Override
	public String getTokenType(){
		return input;
	}

	@Override
	public String toString(){
		return super.toString() + " " + this.input;
	}
}