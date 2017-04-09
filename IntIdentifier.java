public class IntIdentifier extends Token {
	private int val;

	public IntIdentifier(int v, int r, int c){
		super(r, c);
		this.val = v;
	}

	@Override
	public String toString(){
		
		return super.toString() + " INT_IDENTIFIER (" + val + ")";
	}
}