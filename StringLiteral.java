public class StringLiteral extends Token {
	private String value;
	
	public StringLiteral(String i, int r, int c) {
		super("String_Literal", r, c);
		name = i;
	}

	public StringLiteral(int r, int c) {
		super(r, c);
	}

	public void setValue(String v) {
		name = v;
	}

}