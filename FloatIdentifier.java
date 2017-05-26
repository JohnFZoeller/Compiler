public class FloatIdentifier extends Token {

	public FloatIdentifier(float v, int r, int c){
		super("FloatIdentifier", r, c);
		setVal(v);
	}

	@Override
	public Float getVal() {
		return (Float)val;
	}

	@Override
	public String toString(){
		return super.toString() + " FLOAT_IDENTIFIER (" + Float.toString((float)val) + ")";
	}
}