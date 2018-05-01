public class StringLiteral extends Token {
	private String value;
	
	public StringLiteral(String i, int r, int c) {
		super("String_Literal", r, c);
		this.val = i;
		name = i;
	}

	public StringLiteral(int r, int c) {
		super(r, c);
	}

	public void setValue(String v) {
		name = v;
	}

	@Override
	public String toString(){
		return super.toString() + " STRING LITERAL (" + this.val + ")";
	}

	@Override
	public String getVal() {
		return (String)this.val;
	}

}