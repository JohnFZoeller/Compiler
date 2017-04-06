package tokens;

public class StringIdentifier extends Token{
	public StringIdentifier(String s, int r, int c){
		super(s, r, c);
	}

	@Override
	public String toString(){
		return super.toString() + " (" + this.input + ")";
	}
}