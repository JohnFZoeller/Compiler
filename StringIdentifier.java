public class StringIdentifier extends Token{
	public String varName;

	public StringIdentifier(String s, int r, int c){
		super("StringIdentifier", r, c);
		setName(s);
	}
	@Override
	public String getVarName() {
		return name;
	}
	@Override
	public String toString(){
		return super.toString() + " IDENTIFIER (" + this.input + ")";
	}
}