
public class Expressions extends Subtree {
	Expressions(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new Expression(token, it));

		addAllChildren();
	}

	/*
		Recursively adds all expression children to the Expressions
		Node's array list.
	*/

	private void addAllChildren() {
		if(token.getTokenType().equals("COMMA")) {
			match("COMMA");
			addChild(new Expression(token, it));
			addAllChildren();
		} else {
			return;
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row ", " + col + ") " 
			+ System.identityHashCode(this) 
			+ " Expressions");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}