public class StringIdentifier extends Token {

	public StringIdentifier(String s, int r, int c){
		super("StringIdentifier", r, c);
		setName(s);
		setVal(s);
	}

	@Override
	public String getVal() {
		return (String)this.val;
	}

	@Override
	public String toString(){
		return super.toString() + " IDENTIFIER (" + this.input + ")";
	}
}