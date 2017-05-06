public class IntIdentifier extends Token {
	private int val;

	public IntIdentifier(int v, int r, int c){
		super("IntIdentifier", r, c);
		this.val = v;
		setVal(v);
	}

	@Override
	public String toString(){
		
		return super.toString() + " INT_IDENTIFIER (" + val + ")";
	}
}
//