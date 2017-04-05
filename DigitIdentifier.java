package tokens;

public class DigitIdentifier extends Token{
	private int val;

	public DigitIdentifier(int v, int r, int c){
		super(r, c);
		this.val = v;
	}

	@Override
	public String toString(){
		return super.toString() + " (" + val + ")";
	}
}