//package tokens;

public class DigitIdentifier extends Token{
	private int val;

	public DigitIdentifier(int v, int r, int c){
		super(r, c);
		this.val = v;
	}

	//I think there are going to be different cases for
	//digit identifiers (ints, float32's, etc.. )
	//for now just outputting DIGIT IDENTIFIER

	@Override
	public String toString(){
		return super.toString() + " DID (" + val + ")";
	}
}