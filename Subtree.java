import java.io.*;
import java.util.*;

public class Subtree {
	protected int row = 0;
	protected int col = 0;
	protected int level = 0;
	Token token;
	List<Subtree> children;
	Iterator<Token> it;
	String print;

	public Subtree(){
		print = "";
		//collapse
	}

	public Subtree(Token t){
		token = t;
		print = "";
	}

	public Subtree(Iterator<Token> i){ 
		it = i;
		print = "";
	}

	public Subtree(Token current, Iterator<Token> i) {
		this.token = current;
		this.row = current.getRow();
		this.col = current.getCol();
		it = i;
		print = "";
	}

	public void addChild(Subtree subtree) {
		if(children == null) {
			children = new ArrayList<Subtree>();
		}
		children.add(subtree);
		token = children.get(children.size() -1).token;
	}

	public boolean hasChildren(){
		//collapse
		return (children != null);
	}

	public void print(){;}

	public void print(int a){;}

	public void printTree() {
		System.out.println("root node " + System.identityHashCode(this));

		if(hasChildren()){
			System.out.println("(0, 0) "
				+ System.identityHashCode(children)
				+ " list");

			printHelp();
		}

		// int count = 0;
		// while(count < level) {
		// 	toPrint += toPrint;
		// 	count++;
		// }
		// toPrint += "(" + row + ", " + col + ") ";
		// System.out.println(toPrint);
	}

	public void printHelp(){
		print += "+---";

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}

	public void printUp(String p){
		print += p;
		//collapse
	}


	public void match(String expect){
		if(token.getTokenType().equals(expect)){
			readNextTok();
		}
		else throw new 
			Error("Expected: " + expect + " Got: " + token.getTokenType());
	}

	public void readNextTok() {
		if(it.hasNext()){
			token = it.next();

			if(token != null){

				System.err.println(token.getTokenType() + " " 
					+ token.getName());
			}
		}
		else 
			return;
	}

	public int runningCol(){
		// col = (currentTok.getCol() < col) ? 
		// (col + currentTok.getCol()) : currentTok.getCol();

		return col;
	}

	public boolean isExpresh(){ return true; }

	public boolean hasWilds(){ return false; }

	public boolean isDimensh(){ return false; }

	public boolean hasFieldDecls() { return false; }

	public boolean isElse(){ return false; }

	public boolean hasParams(){ 
		//collapse
		return !token.getTokenType().equals("CLOSE_PARENTHESIS"); 
	}

	public boolean hasTypeDesc(){ return true; }
}

/************************Main Node Types********************/

class For extends Subtree{
	For(Token t, Iterator<Token> i){
		super(t, i);

		match("for");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));
		//token = children.get(0).token;
		match("SEMICOLON");

		addChild(new Expression(token, it));
		//token = children.get(1).token;
		match("SEMICOLON");

		addChild(new Expression(token, it));
		//token = children.get(2).token;
		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));
		//token = children.get(3).token;
	}
}

class While extends Subtree{
	While(Token t, Iterator<Token> i){
		super(t, i);

		match("while");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));
		//token = children.get(0).token;

		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));
		//token = children.get(1).token;
	}
}

class If extends Subtree{
	If(Token t, Iterator<Token> i){
		super(t, i);

		match("if");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));

		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));

		if(isElse())
			addChild(new Else(token, it));
	}
}

class Else extends Subtree{
	Else(Token t, Iterator<Token> i){
		super(t, i);
		match("else");
		addChild(new Block(token, it));
	}
}

class Func extends Subtree{
	Func(Token t, Iterator<Token> i){
		super(t, i);

		int j = 1;
		match("function");

		addChild(new Symbol(token));
		match("StringIdentifier");
		match("OPEN_PARENTHESIS");

		if(hasParams()){
			addChild(new Params(token, it));
			//token = children.get(j++).token;
		}

		match("CLOSE_PARENTHESIS");

		if(hasTypeDesc()){
			addChild(new TypeDescriptor(token, it));
			//token = children.get(j++).token;
		}

		addChild(new Block(token, it));
		//token = children.get(j).token;
	}

	@Override
	public void print(){
		int i = 0;

		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");
		
		System.out.println(print + "name");
		children.get(i).printUp(print);
		children.get(i++).print(System.identityHashCode(this));


		System.out.println(print + "parameters");
		if(children.get(i) instanceof Params){
			children.get(i).printUp(print);
			children.get(i++).print();
		}


		System.out.println(print + "return type");
		if(children.get(i) instanceof TypeDescriptor){
			children.get(i).printUp(print);
			children.get(i++).print();
		}

		System.out.println(print + "body");
		children.get(i).printUp(print);
		children.get(i).print();

	}
}

class Var extends Subtree{
	public Var(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("static"))
			match("static");
		
		if(token.getTokenType().equals("const"))
			match("const");

		match("var");

		addChild(new Symbol(token));
		match("StringIdentifier");

		//assignment case ELSE declaration case
		if(token.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			addChild(new Expression(token, it));
		} 
		else {
			addChild(new TypeDescriptor(token, it));
			//token = children.get(1).token;
		}

		match("SEMICOLON");
	}

	@Override
	public void print(){
		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");
		
		System.out.println(print + "name");
		children.get(0).printUp(print);
		children.get(0).print(System.identityHashCode(this));

		children.get(1).printUp(print);

		System.out.println(print + "type");
		if(children.get(1) instanceof TypeDescriptor){
			children.get(1).print();
		}

		System.out.println(print + "initializer");
		if(children.get(1) instanceof Expression){
			children.get(1).print();
		}
	}
}

class Retur extends Subtree{
	public Retur(Token t, Iterator<Token> i){
		super(t, i);
		match("return");

		if(isExpresh())
			addChild(new Expression(token, it));

		if(!token.getTokenType().equals("SEMICOLON"))
			throw new Error("Expected SEMICOLON, GOT: " + token.getTokenType());
	}
}

class Print extends Subtree{
	public Print(Token t, Iterator<Token> i){
		super(t, i);
		match("print");

		addChild(new Expression(token, it));
		match("StringIdentifier");

		if(!token.getTokenType().equals("SEMICOLON"))
			throw new Error("Expected SEMICOLON, GOT: " + token.getTokenType());
	}
}

class Exit extends Subtree{
	public Exit(Token t, Iterator<Token> i){
		super(t, i);
		match("exit");

		if(isExpresh())
			addChild(new Expression(token, it));

		if(!token.getTokenType().equals("SEMICOLON"))
			throw new Error("Expected SEMICOLON, GOT: " + token.getTokenType());
	}
}

class Type extends Subtree{
	Type(Token t, Iterator<Token> i){
		super(t, i);

		match("type");

		addChild(new Symbol(token));
		match("StringIdentifier");

		addChild(new TypeDescriptor(token, it));

		if(!token.getTokenType().equals("SEMICOLON"))
			throw new Error("Expected SEMICOLON, GOT: " + token.getTokenType());
	}
}



/*********************Support Statements**********************/

class Block extends Subtree{
	Block(Token t, Iterator<Token> i){
		super(t, i);

		match("OPEN_BRACE");

		for(int j = 0; !token.getTokenType().equals("CLOSE_BRACE"); j++){
			match();
			//token = children.get(j).token;
		}

		match("CLOSE_BRACE");
	}

	public void match(){
		switch(token.getTokenType()) {
			case "for":		addChild(new For(token, it));
							break;
			case "while":	addChild(new While(token, it));
							break;
			case "if":		addChild(new If(token, it));
							break;
			case "print":	addChild(new Print(token, it));
							break;
			case "return":	addChild(new Retur(token, it));
							break;
			case "type":	addChild(new Type(token, it));
							break;
			case "exit":	addChild(new Exit(token, it));
							break;
			case "function":addChild(new Func(token, it));
							break;
			case "var":		addChild(new Var(token, it));
							break;
			case "static":	addChild(new Var(token, it));
							break;
			case "const":	addChild(new Var(token, it));
							break;
			default:		System.out.println("Unrecognized token type "
				+ token.getTokenType());
							System.exit(0);
							break;
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " list");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print + "+---");
			children.get(i).print();
		}
	}
}

class TypeDescriptor extends Subtree{
	TypeDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new NaTypeDescriptor(token, it));
		//token = children.get(0).token;

		if(isDimensh())								//optional expression next?
			addChild(new Dimension(token, it));	
	}

	@Override 
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ System.identityHashCode(token)
			+ " type: " 
			+ children.get(0).children.get(0).token.getTokenType());
	}
}

class NaTypeDescriptor extends Subtree{
	NaTypeDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("record")){
			addChild(new RecordDescriptor(token, it));
			//token = children.get(0).token;
		}
		else if(token.getTokenType().equals("StringIdentifier")){
			addChild(new Symbol(token));
			match("StringIdentifier");
		}
		else{
			addChild(new BasicType(token));
			switch(token.getTokenType()){
				case "byte" : 	match("byte"); break;
				case "int32": 	match("int32"); break;
				case "float64": match("float64"); break;
				default : break;
			}

		}
	}

	@Override 
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) "
			+ System.identityHashCode(token)
			+ " type: " 
			+ children.get(0).token.getTokenType());
	}
}

class RecordDescriptor extends Subtree{
	RecordDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		match("record");

		if(hasFieldDecls())
			addChild(new FieldDeclarations(token, it));

		match("end");
	}
}

class FieldDeclarations extends Subtree{
	FieldDeclarations(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new FieldDeclaration(token, it));
		y();
	}

	public void y(){
		if(token.getTokenType().equals("end")) return;

		match("COMMA");

		addChild(new FieldDeclaration(token, it));
		y();
	}
}

class FieldDeclaration extends Subtree{
	FieldDeclaration(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new Symbol(token));
		match("StringIdentifier");

		addChild(new TypeDescriptor(token, it));
	}
}

class Dimension extends Subtree{
	Dimension(Token t, Iterator<Token> i){
		super(t, i);

		match("OPEN_BRACKET");
		addChild(new Expressions(token, it));
		match("CLOSE_BRACKET");
	}
}

class Params extends Subtree{
	Params(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new Param(token, it));
		//token = children.get(0).token;

		x();
	}

	public void x(){
		if(!token.getTokenType().equals("COMMA")) return;

		match("COMMA");

		Param p = new Param(token, it);
		addChild(p);
		//token = p.token;

		x();
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " list");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}

class Param extends Subtree{
	Param(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("ref"))
			match("ref");

		if(token.getTokenType().equals("const"))
			match("const");

		addChild(new Symbol(token));
		match("StringIdentifier");

		if(token.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			addChild(new Expression(token, it));
		}
		else{
			addChild(new NaTypeDescriptor(token, it));

			if(hasWilds()){
				match("OPEN_BRACKET");
				//dimWilds();
				match("CLOSE_BRACKET");
			}
		}

		//token = children.get(1).token;
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " Parameter");

		printUp("+---");

		System.out.println(print + "name");
		children.get(0).printUp(print);
		children.get(0).print(System.identityHashCode(this));

		System.out.println(print + "type");
		if(children.get(1) instanceof NaTypeDescriptor){
			children.get(1).printUp(print);
			children.get(1).print();
		}

		System.out.println(print + "initializer");
	}
}

/***********************Output Helpers********************/

class Symbol extends Subtree{
	Symbol(Token t){
		token = t;
	}

	@Override
	public void print(int a){
		printUp("+---");

		System.out.println(print + "(,) "
			+ System.identityHashCode(token) + " "
			+ this.getClass() + ": " + token.getName() +
			" (" + token.getTokenType() + ") defined at: " + a);
	}
}

class BasicType extends Subtree{
	BasicType(Token t){
		token = t;
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println();
	}
}

/*********************Expressions*************************/

class Expressions extends Subtree{
	Expressions(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class Expression extends Subtree{
	Expression(Token t, Iterator<Token> i){
		super(t, i);
	}
	//collapse

	@Override 
	public void print(){

	}
}