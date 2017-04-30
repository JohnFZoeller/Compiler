//package tokens;

public class StringIdentifier extends Token{
	public String varName;

	public StringIdentifier(String s, int r, int c){
		super("StringIdentifier", r, c);
		varName = s;
		setName(varName);
	}

	@Override
	public String toString(){
		return super.toString() + " IDENTIFIER (" + this.input + ")";
	}
}