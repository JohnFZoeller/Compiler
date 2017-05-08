public class CharLiteral extends Token {
	private char value;
	public CharLiteral(char i, int r, int c) {
		super("Char_Literal", r, c);
		value = i;
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
}