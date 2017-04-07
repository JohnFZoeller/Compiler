public class Op extends Token{
	public Op(String output, int r, int c){
		super(output, r, c);
	}

	@Override
	public String toString(){
		return super.toString() + " " + this.input;
	}
}