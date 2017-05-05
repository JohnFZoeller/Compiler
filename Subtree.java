import java.io.*;
import java.util.*;

public class Subtree {
	protected int row = 0;
	protected int col = 0;
	protected int level = 0;
	Token token;
	List<Subtree> children;
	Iterator<Token> it;
	String print = "";

	public Subtree(){
	}

	public Subtree(Token t){
		token = t;
	}

	public Subtree(Iterator<Token> i){ 
		it = i;
	}

	public Subtree(Token current, Iterator<Token> i) {
		this.token = current;
		this.row = current.getRow();
		this.col = current.getCol();
		it = i;
	}

	public void addChild(Subtree subtree) {
		if(children == null) {
			children = new ArrayList<Subtree>();
		}
		children.add(subtree);
		token = children.get(children.size() - 1).token;
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

	public boolean hasWilds(){
		return token.getTokenType().equals("OPEN_BRACKET");
		//collapse
	}

	public boolean isDimensh(){
		return token.getTokenType().equals("OPEN_BRACKET");
		//collapse
	}

	public boolean isElse(){
		return token.getTokenType().equals("else");
		//collapse
	}

	public boolean hasParams(){ 
		//collapse
		return !token.getTokenType().equals("CLOSE_PARENTHESIS"); 
	}

	public boolean hasTypeDesc(){ 
		return !token.getTokenType().equals("OPEN_BRACE");
		//collapse
	}
}

/************************Main Node Types********************/

class For extends Subtree{
	For(Token t, Iterator<Token> i){
		super(t, i);

		match("for");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));
		match("SEMICOLON");

		addChild(new Expression(token, it));
		match("SEMICOLON");

		addChild(new Expression(token, it));
		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));
	}

	@Override
	public void print(){
	}
}

class While extends Subtree{
	While(Token t, Iterator<Token> i){
		super(t, i);

		match("while");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));

		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));
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

		match("function");

		addChild(new Symbol(token));
		match("StringIdentifier");
		match("OPEN_PARENTHESIS");

		if(hasParams())
			addChild(new Params(token, it));

		match("CLOSE_PARENTHESIS");

		if(hasTypeDesc())
			addChild(new TypeDescriptor(token, it));

		addChild(new Block(token, it));
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
		} else {
			addChild(new TypeDescriptor(token, it));
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

		match("SEMICOLON");
	}

	@Override 
	public void print(){
		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		if(hasChildren()){
			children.get(0).printUp(print + "+---");
			children.get(0).print();
		}
	}
}

class Print extends Subtree{
	public Print(Token t, Iterator<Token> i){
		super(t, i);
		match("print");

		addChild(new Expression(token, it));
		match("StringIdentifier");

		match("SEMICOLON");
	}

	@Override 
	public void print(){
		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		children.get(0).printUp(print + "+---");
		children.get(0).print();
	}
}

class Exit extends Subtree{
	public Exit(Token t, Iterator<Token> i){
		super(t, i);
		match("exit");

		if(isExpresh())
			addChild(new Expression(token, it));

		match("SEMICOLON");
	}

	@Override 
	public void print(){
		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		if(hasChildren()){
			children.get(0).printUp(print + "+---");
			children.get(0).print();
		}
	}
}

class Type extends Subtree{
	Type(Token t, Iterator<Token> i){
		super(t, i);

		match("type");

		addChild(new Symbol(token));
		match("StringIdentifier");

		addChild(new TypeDescriptor(token, it));

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

	}
}


/*********************Support Statements**********************/

class Block extends Subtree{
	Block(Token t, Iterator<Token> i){
		super(t, i);

		match("OPEN_BRACE");

		for(int j = 0; !token.getTokenType().equals("CLOSE_BRACE"); j++){
			match();
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

		x();
	}

	public void x(){
		if(!token.getTokenType().equals("COMMA")) return;

		match("COMMA");
		addChild(new Param(token, it));

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

class DimWilds extends Subtree{
	DimWilds(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new WildCard(token, it));
		y();
	}

	public void y(){
		if(!token.getTokenType().equals("COMMA")) return;

		match("COMMA");
		addChild(new WildCard(token, it));
		y();
	}

	@Override 
	public void print(){

	}
}

class WildCard extends Subtree{
	WildCard(Token t, Iterator<Token> i){
		super(t, i);

		match("ASTERISK");
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

/*********************Expressions************************/


class Expressions extends Subtree {
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
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", " + col + ") " 
			+ System.identityHashCode(this) 
			+ " Expressions");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}

class Expression extends Subtree {
	Expression(Token t, Iterator<Token> i){
		super(t, i);
		addAllChildren();
	}

	private boolean isUnary() {
		boolean isUnary = false;
		if(token.getTokenType().equals("MINUS") ||
			token.getTokenType().equals("EXCLAMATION_POINT") ||
			token.getTokenType().equals("TILDE")) {
			isUnary = true;
		}
		return isUnary;
	}

	private void addAllChildren() {
		if(isUnary()) {
			addChild(new ExprRest(token, it));
		} else {
			matchOperand();
			addChild(new ExprRest(token, it));
		}

		if(token.getTokenType().equals("COMMA")) {
			addAllChildren();
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", " + col + ") " 
			+ System.identityHashCode(this) 
			+ token.getTokenType());

		for(int i = 0; i < children.size(); i++){
		 	children.get(i).printUp(print);
		 	children.get(i).print();
		}
	}

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "INT_IDENTIFIER":
				match("INT_IDENTIFIER");
				break;
			case "FLOAT_IDENTIFIER":
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				match("BYTE_IDENTIFIER");
				break;
			case "StringIdentifier":
				match("StringIdentifier");
				break;
			case "KEYWORD_INT32":
				match("KEYWORD_INT32");
				match("OPEN_PARENTHESIS");
				addChild(new Expression(token, it));
				break;
			case "KEYWORD_FLOAT":
				match("KEYWORD_FLOAT");
				match("OPEN_PARENTHESIS");
				addChild(new Expression(token, it));
				break;
			case "KEYWORD_BYTE":
				match("KEYWORD_BYTE");
				match("OPEN_PARENTHESIS");
				addChild(new Expression(token, it));
				break;
			default:
				break;
		}
	}
}

class ExprRest extends Subtree {
	Token operator = null;
	Token operand = null;

	ExprRest(Token t, Iterator<Token> i){
		super(t, i);
		addChildren();
	}

	private void addChildren() {
		matchOperator();
		if(token.getTokenType().equals("OPEN_PARENTHESIS")) {
			addChild(new ExprRest(token, it));
		} else {
			matchOperand();
		}
	}

	private void matchOperator() {
		switch(token.getTokenType()) {
			case "PLUS":
				operator = token;
				match("PLUS");
				break;
			case "MINUS":
				operator = token;
				match("MINUS");
				break;
			case "ASTERISK":
				operator = token;
				match("ASTERISK");
				break;
			case "BACKSLASH":
				operator = token;
				match("BACKSLASH");
				break;
			case "TILDE":
				operator = token;
				match("TILDE");
				break;
			case "ASSIGNMENT_OPERATOR":
				operator = token;
				match("ASSIGNMENT_OPERATOR");
				break;
			case "RELATIONAL_GREATER_THAN":
				operator = token;
				match("RELATIONAL_GREATER_THAN");
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				operator = token;
				match("RELATIONAL_GREATER_EQUALTO");
				break;
			case "RELATIONAL_LESS_THAN":
				operator = token;
				match("RELATIONAL_LESS_THAN");
				break;
			case "RELATIONAL_LESS_EQUALTO":
				operator = token;
				match("RELATIONAL_LESS_EQUALTO");
				break;
			case "BITWISE_AND":
				operator = token;
				match("BITWISE_AND");
				break;
			case "LOGICAL_AND":
				operator = token;
				match("LOGICAL_AND");
				break;
			case "BITWISE_OR":
				operator = token;
				match("BITWISE_OR");
				break;
			case "LOGICAL_OR":
				operator = token;
				match("LOGICAL_OR");
				break;
			case "EXCLAMATION_POINT":
				operator = token;
				match("EXCLAMATION_POINT");
				break;
			case "BITWISE_XOR":
				operator = token;
				match("BITWISE_XOR");
				break;
			case "LOGICAL_NOT":
				operator = token;
				match("LOGICAL_NOT");
				break;
			case "OUTPUT":
				operator = token;
				match("OUTPUT");
				break;
			case "INPUT":
				operator = token;
				match("INPUT");
				break;
			case "EQUALITY":
				operator = token;
				match("EQUALITY");
				break;
			default:
				break;
		}
	}

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "INT_IDENTIFIER":
				operand = token;
				match("INT_IDENTIFIER");
				break;
			case "FLOAT_IDENTIFIER":
				operand = token;
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				operand = token;
				match("BYTE_IDENTIFIER");
				break;
			case "StringIdentifier":
				operand = token;
				match("StringIdentifier");
				break;
			case "KEYWORD_INT32":
				operand = token;
				match("KEYWORD_INT32");
				break;
			case "KEYWORD_FLOAT":
				operand = token;
				match("KEYWORD_FLOAT");
				break;
			case "KEYWORD_BYTE":
				operand = token;
				match("KEYWORD_BYTE");
				break;
			default:
				break;
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", " + col + ") " 
			+ System.identityHashCode(this) 
			+ "Operator: " + operator.getTokenType() + " , Operand: "
			+ operator.getTokenType());

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
		
}