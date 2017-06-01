public class IntIdentifier extends Token {

	public IntIdentifier(int v, int r, int c){
		super("IntIdentifier", r, c);
		setVal(v);
		this.intValue = v;
	}

	public IntIdentifier(Token toCopy) {
		super(toCopy);
		this.val = new Integer((Integer)toCopy.val);
	}

	@Override
	public Integer getVal(){
		return (Integer)val;
	}


	@Override
	public String toString(){
		
		return super.toString() + " INT_IDENTIFIER (" + Integer.toString((int)val) + ")";
	}
}