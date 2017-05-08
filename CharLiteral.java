public class CharLiteral extends Token {
	private char value;
	public CharLiteral(String i, int r, int c) {
		super(i, r, c);
	}

	public CharLiteral(char v, int r, int c) {
		super(r, c);
		value = v;
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