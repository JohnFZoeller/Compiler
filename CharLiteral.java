public class CharLiteral extends Token {
	private char value;
	
	public CharLiteral(char i, int r, int c) {
		super("Char_Literal", r, c);
		value = i;
		setVal(i);
	}

	public CharLiteral(int r, int c) {
		super(r, c);
	}

	public void setValue(char v) {
		value = v;
	}

	public char getValue() {
		return value;
	}

	@Override
	public String toString(){
		return super.toString() + " CHARACTER LITERAL (" + this.val + ")";
	}

	@Override
	public Character getVal() {
		return (Character)this.val;
	}
}