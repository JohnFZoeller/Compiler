public class StringIdentifier extends Token {

	public StringIdentifier(String s, int r, int c){
		super("StringIdentifier", r, c);
		setName(s);
	}

	@Override
	public String toString(){
		return super.toString() + " IDENTIFIER (" + this.input + ")";
	}
}