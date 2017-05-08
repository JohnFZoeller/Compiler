public class StringLiteral extends Token {
	private String value;
	
	public StringLiteral(String i, int r, int c) {
		super("String_Literal", r, c);
		value = i;
	}

	public StringLiteral(int r, int c) {
		super(r, c);
	}

	public void setValue(String v) {
		value = v;
	}

	public String getValue() {
		return value;
	}
}