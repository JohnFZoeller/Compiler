public class FloatIdentifier extends Token {
	private float value;

	public FloatIdentifier(float v, int r, int c){
		super(r, c);
		this.value = v;
	}

	@Override
	public String toString(){
		
		return super.toString() + " FLOAT_IDENTIFIER (" + value + ")";
	}
}