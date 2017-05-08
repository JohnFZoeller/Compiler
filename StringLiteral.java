public class StringLiteral extends Token {
	private String value;
	
	public StringLiteral(String i, int r, int c) {
		super(i, r, c);
	}

	public StringLiteral(int r, int c) {
		super(r, c);
	}

	public String setValue(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}
}