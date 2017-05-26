import java.io.*;
import java.util.*;

/*
 *	Look at typeCheck function defined in Subtree and Block class.
		The first pass
 *	will define the different scopes encountered and create Symbol Tables
 *	for each scope, the second pass will be populating the declared scope's
 *	symbol tables with the identifiers found in that scope.
 *
 */

public class Subtree {
	protected int row = 0;
	protected int col = 0;
	protected int level = 0;
	Token token = null;
	List<Subtree> children = null;
	Iterator<Token> it = null;
	String print = "";

	Scope currentScope = null;
	Symbol symbol = null;
	SymbolType type = null;

	public Subtree(){}

	public Subtree(Iterator<Token> i){ 
		it = i;
		//collapse;
	}

	public Subtree(Token current, Iterator<Token> i) {
		this.token = current;
		this.row = current.getRow();
		this.col = current.getCol();
		it = i;
	}


	public SymbolType getSymType(){
		return type;
	}

	public void addChild(Subtree subtree) {
		if(children == null)
			children = new ArrayList<Subtree>();

		children.add(subtree);
		token = children.get(children.size() - 1).token;
	}

	public boolean hasChildren(){
		return (children != null);
	}

	public void decorateFirst(Scope e) throws AlreadyDefinedException, UndefinedTypeException{;}

	public void decorateSecond(Scope e) throws UndefinedTypeException, AlreadyDefinedException {;}

	public void beginDecorateFirst(SymbolTable mainTable) throws AlreadyDefinedException, UndefinedTypeException{
		currentScope = mainTable.globals;

		for(int i = 0; i < children.size(); i++){
			children.get(i).decorateFirst(currentScope);
		}
	}

	public void beginDecorateSecond(SymbolTable mainTable)throws AlreadyDefinedException, UndefinedTypeException{

		for(int i = 0; i < children.size(); i++){
			 children.get(i).decorateSecond(null);
		}
	}

	public void print(){;}

	public void print(int a){;}

	public void printTree() {
		System.out.println("root node " + System.identityHashCode(this));

		if(hasChildren()){
			System.out.println("(0, 0) "
				+ System.identityHashCode(children)
				+ " list");

			print += "+---";

			for(int i = 0; i < children.size(); i++){
				children.get(i).printUp(print);
				children.get(i).print();
			}		
		}
	}

	public void printUp(String p){
		print += p;
		//collapse
	}

	public void match(String expect){
		if(token.getTokenType().equals(expect)){
			readNextTok();
			if(token == null) {
				readNextTok();
			}
		} else {
			throw new Error("Expected: " + expect + " Got: " + token.getTokenType());
		}
	}

	public void readNextTok() {
		if(it.hasNext()){
			token = it.next();
			if(token != null) {
				System.err.println(token.getTokenType() + " " 
					+ token.getName());
			}
		} else {
			return;
		}
	}

	public int runningCol(){
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

	public String toPrint() {
		return "";
	}

	public void setType(Scope e){;}  //temporary workaround

}

/*------------------------------ Main Node Types ---------------------------------*/

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
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");

		System.out.println(print + " init");
		children.get(0).printUp(print);
		children.get(0).print();

		System.out.println(print + " test");
		children.get(1).printUp(print);
		children.get(1).print();

		System.out.println(print + " incr");
		children.get(2).printUp(print);
		children.get(2).print();

		System.out.println(print + " body");
		children.get(3).printUp(print);
		children.get(3).print();
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

	@Override
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");

		System.out.println(print + " test");
		children.get(0).printUp(print);
		children.get(0).print();

		System.out.println(print + " body");
		children.get(1).printUp(print);
		children.get(1).print();
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

	@Override
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");

		System.out.println(print + " test");
		children.get(0).printUp(print);
		children.get(0).print();

		System.out.println(print + " body");
		children.get(1).printUp(print);
		children.get(1).print();

		if(children.size() == 3){
			System.out.println(print + " else");
			children.get(2).printUp(print);
			children.get(2).print();
		}

	}
}

class Else extends Subtree{
	Else(Token t, Iterator<Token> i){
		super(t, i);
		match("else");
		addChild(new Block(token, it));
	}

	@Override
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");

		System.out.println(print + " body");
		children.get(0).printUp(print);
		children.get(0).print();
	}
}

/*----------------------------- Function Class --------------------------------
 *
 *	Function class is an extension of Subtree. Class is setup to model a 
 *	function declaration with the grammar specifications outlined in the
 *	CSS 448 Programming Language
 *
 *---------------------------------------------------------------------------*/

// !----- DECORATE FIRST AND DECORATE SECOND SHOULD BE DONE FOR FUNCTION -------!

class Function extends Subtree {
	public boolean [] flags = new boolean[2]; //[0] = params, [1] = type;
	LocalScope localScope;
	Block block;

	Function(Token t, Iterator<Token> i) {
		super(t, i);								//calls super constructor
		match("function");							//matches type

		addChild(new Name(token));
		match("StringIdentifier");					//increments Iterator
		match("OPEN_PARENTHESIS");					//increments Iterator, enter Params

		if(hasParams()) {							//checks if Params are present
			addChild(new Params(token, it));		//adds child Nodes accordingly
			flags[0] = true;
		}

		match("CLOSE_PARENTHESIS");					//increments Iterator

		if(hasTypeDesc()) {							//checks for type descriptor
			addChild(new TypeDescriptor(token, it));//adds child node accordingly
			flags[1] = true;
		}

		addChild(new Block(token, it));				//final child, Block
	}

	/*
	 *	Semantic Rules for Parameters:
	 *	1. All parameters of type var must be pass by reference
	 *	2. All parameters not of type var must be pass-by-value
	 *	3. If a formal parameter has a default value, all others must as well
	 *	
	 *	We want each parameter to return whether or not it is a var (if so it must
	 *	be pass by reference), and if the value is initialized. This will be denoted
	 *	by whether or not the pattern id value is followed.
	 *
	 */

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException {
		String funcName = children.get(0).token.getName();
		Symbol previouslyDefined = enclosing.resolve(funcName);
		Subtree currentNode;
		String symType;
		currentScope = enclosing;

		if(previouslyDefined != null) {
			throw new AlreadyDefinedException(token.getTokenType());
		} else {
			if(!flags[1]) {														//no type
				type = (BuiltInTypeSymbol)enclosing.resolve("void");			//type = void
				currentNode = (flags[0]) ? children.get(1) : null;				//cur = params
			} else {															//is a type
				currentNode = (flags[0]) ? children.get(2) : children.get(1);	//cur = params
				symType = ((TypeDescriptor)currentNode).returnType();			//type
				previouslyDefined = enclosing.resolve(symType);					//ref type

				if(previouslyDefined instanceof TypeSymbol)						//user type?
					symType = previouslyDefined.type.getTypeName();				//get type Name

				if(previouslyDefined == null) {									//not defined
					throw new UndefinedTypeException(symType);
				}else{
					type = (symType == "record") ? (RecordSymbol)enclosing.resolve(symType) :
						(BuiltInTypeSymbol)enclosing.resolve(symType);
				}
			}
			symbol = new FunctionSymbol(funcName, type, enclosing);
			enclosing.define(symbol);

			if(flags[0])
				symbol.addParams(children.get(1).children);
		}
	}

	@Override
	public void decorateSecond(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		localScope = new LocalScope((FunctionSymbol)symbol);

		if(flags[0] && flags[1])
			block = (Block)children.get(3);
		else if((flags[0] && !flags[1]) || (!flags[0] && flags[1]))
			block = (Block)children.get(2);
		else block = (Block)children.get(1);

		block.decorateFirst(localScope);
	}

	@Override
	public void print(){
		int i = 0;

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
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
	public boolean [] locks = new boolean[2];//[0] = static, [1] = const;

	public Var(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("static")){
			match("static");
			locks[0] = true;
		}
		
		if(token.getTokenType().equals("const")){
			match("const");
			locks[1] = true;
		}

		match("var");

		addChild(new Name(token));
		match("StringIdentifier");

		if(token.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			addChild(new Expression(token, it));
		} else {
			addChild(new TypeDescriptor(token, it));
		}

		match("SEMICOLON");
	}

	/*
	 *	or we want to store the identifier and the type descriptor of the
	 *	expression, as the initializing will be done in the second pass
	 */

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		String symbolName = children.get(0).token.getName();
		Symbol previouslyDefined = enclosing.resolve(symbolName);
		String symType;
		currentScope = enclosing;

		if(previouslyDefined != null){
			throw new AlreadyDefinedException(previouslyDefined.getName());
		} else {
			if(children.get(1) instanceof TypeDescriptor){
				Subtree nodeType = children.get(1).children.get(0).children.get(0);

				if(nodeType instanceof BasicType){
					type = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
					symbol = new VarSymbol(symbolName, type, locks);
				}
				else if(nodeType instanceof Name){//fix me to include records
					previouslyDefined = enclosing.resolve(nodeType.token.getName());
					symType = previouslyDefined.getType().getTypeName();

					if(previouslyDefined == null)
						throw new UndefinedTypeException(children.get(1).token.getName());
					else{
						type = (symType == "record") ? (RecordSymbol)previouslyDefined.getType() :
						(BuiltInTypeSymbol)previouslyDefined.getType();

						symbol = new VarSymbol(symbolName, type, locks);
					}
				}
				else if(nodeType instanceof RecordDescriptor){
					RecordSymbol record = new RecordSymbol(symbolName, enclosing, 
						nodeType.children.get(0).children);

					type = (RecordSymbol)enclosing.resolve("record");
					symbol = new VarSymbol(symbolName, type, record, locks);
				}
			}
			else if(children.get(1) instanceof Expression){
				children.get(1).decorateFirst(currentScope);
				type = children.get(1).type;
				symbol = new VarSymbol(symbolName, type, locks);
			}
			enclosing.define(symbol);
		}		
	}

	//actually unnecessary- here's why: Any var that is declared with an expression 
	//is still given a type (the type of the expression); and we don't actually store 
	//the value of that expression in the symbol table- just its type. 
	public void decorateSecond(Scope cur) throws UndefinedTypeException, AlreadyDefinedException{;}
	//When he said the second pass is for initializations, what I think he was 
	//referring to is this kind of case...

	//var zoel = 5;		-> zoel.type = int32;  -> still technically a declaration
	//var john int32;	-> john.type = int32;
	//john = zoel;		-> verify john.type == zoel.type;

	//the above case is therefore a decoration of an expression, because a line that
	//starts with a stringIdentifer is not a declaration of any kind. This makes
	//me realize that I have written very little on this iteration and would like to 
	//steal some of your work (-:



	@Override
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
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
		System.out.println(print + "(" + row + ", "
			+ col + ") "
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
		System.out.println(token.getName());
		addChild(new Expression(token, it));
		this.symbol = new Symbol("Print Statement");
		match("SEMICOLON");
	}

	@Override 
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		children.get(0).printUp(print);
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
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		if(hasChildren()){
			children.get(0).printUp(print + "+---");
			children.get(0).print();
		}
	}
}

// !------------------- DECORATE FIRST SHOULD BE DONE FOR TYPE ---------------------!
// !--------------------- DOES NOT REQUIRE DECORATE SECOND -------------------------!

class Type extends Subtree{
	Type(Token t, Iterator<Token> i){
		super(t, i);

		match("type");

		addChild(new Name(token));
		match("StringIdentifier");

		addChild(new TypeDescriptor(token, it));

		match("SEMICOLON");
	}

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		String symbolName = children.get(0).token.getName();
		String symType;
		Symbol previouslyDefined = enclosing.resolve(symbolName);
		currentScope = enclosing;

		if(previouslyDefined != null){
			throw new AlreadyDefinedException(previouslyDefined.getName());
		} else {
			Subtree nodeType = children.get(1).children.get(0).children.get(0);

			if(nodeType instanceof BasicType){
				type = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
				symbol = new TypeSymbol(symbolName, type);
			}
			else if(nodeType instanceof Name){//fix me tpo include records
				previouslyDefined = enclosing.resolve(nodeType.token.getName());
				symType = previouslyDefined.getType().getTypeName();

				if(previouslyDefined == null)
					throw new UndefinedTypeException(children.get(1).token.getName());
				else{
					type = (symType == "record") ? (RecordSymbol)previouslyDefined.getType() :
					(BuiltInTypeSymbol)previouslyDefined.getType();

					symbol = new TypeSymbol(symbolName, type);
				}
			}
			else if(nodeType instanceof RecordDescriptor){
				RecordSymbol record = new RecordSymbol(symbolName, enclosing, 
					nodeType.children.get(0).children);

				type = (RecordSymbol)enclosing.resolve("record");
				symbol = new TypeSymbol(symbolName, type, record);
			}
			enclosing.define(symbol); 
		}	
	}

	//empty operation 
	public void decorateSecond(Scope cur) throws UndefinedTypeException, AlreadyDefinedException{
		System.out.println("debugging: type decorateSecond() called");
	}

	@Override 
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") "  
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		printUp("+---");
		
		System.out.println(print + "name");
		children.get(0).printUp(print);
		children.get(0).print(System.identityHashCode(this));

		System.out.println(print + "type");
		children.get(1).printUp(print);
		children.get(1).print();

	}
}


/*----------------------------- Support Statements -------------------------------*/

// !--------------------- DECORATE BLOCK NEEDS TO BE FINISHED ---------------------!

class Block extends Subtree {

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
			case "function":addChild(new Function(token, it));
							break;
			case "var":		addChild(new Var(token, it));
							break;
			case "static":	addChild(new Var(token, it));
							break;
			case "const":	addChild(new Var(token, it));
							break;
			case "StringIdentifier":
							addChild(new Expression(token, it));
							match("SEMICOLON");
							break;
			default:		System.out.println("Unrecognized token type "
							+ token.getTokenType());
							System.exit(0);
							break;
		}
	}

	@Override
	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		//String funcName = children.get(0).token.getName();
		//Symbol previouslyDefined = enclosing.resolve(funcName);
		//Subtree currentNode;
		//String symType;
		//currentScope = enclosing;


		//iterates over the children of block and adds them to the local scope if
		//they are declarations. Or checks if the type is valid in the wider scope
		//if a type is being used that is not locally defined.
		//
		//	STILL NEEDS TO BE COMPLETED
		//	
		Subtree currentNode;
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);

			if(currentNode instanceof For) {				//adds new scope

			} else if(currentNode instanceof While) {		//adds new scope

			} else if(currentNode instanceof If) {			//adds new scope

			} else if(currentNode instanceof Function) {	//adds new scope

			} else if(currentNode instanceof Print) {

			} else if(currentNode instanceof Type){

			} else if(currentNode instanceof Var) {

			} else if(currentNode instanceof Exit) {

			} else if(currentNode instanceof Retur) {

			} else if(currentNode instanceof Expression) {

			}
		}
	}

	@Override
	public void decorateSecond(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		//iterates over the children of block and adds them to the local scope if
		//they are declarations. Or checks if the type is valid in the wider scope
		//if a type is being used that is not locally defined.
		//
		//	STILL NEEDS TO BE COMPLETED
		//	

		Subtree currentNode;
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);

			if(currentNode instanceof For) {				//adds new scope

			} else if(currentNode instanceof While) {		//adds new scope

			} else if(currentNode instanceof If) {			//adds new scope

			} else if(currentNode instanceof Function) {	//adds new scope

			} else if(currentNode instanceof Print) {

			} else if(currentNode instanceof Type){

			} else if(currentNode instanceof Var) {

			} else if(currentNode instanceof Exit) {

			} else if(currentNode instanceof Retur) {

			} else if(currentNode instanceof Expression) {

			}
		}	
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " list");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}

// !------------------- TYPE DESCRIPTOR SHOULD BE FINISHED ------------------------!

class TypeDescriptor extends Subtree{
	TypeDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new NaTypeDescriptor(token, it));

		if(isDimensh())								//optional expression next?
			addChild(new Dimension(token, it));	
	}

	public String returnType() {
		NaTypeDescriptor temp = (NaTypeDescriptor) children.get(0);
		return temp.returnType();
	}

	@Override 
	public void print(){
		children.get(0).printUp(print);
		children.get(0).print();

		if(children.size() == 2){
			children.get(1).printUp(print);
			children.get(1).print();
		}
	}
}

// !------------------ NATYPEDESCRIPTOR SHOULD BE FINISHED ------------------------!

class NaTypeDescriptor extends Subtree {
	String symbolType = "";
	NaTypeDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("record")){
			symbolType = "record";
			addChild(new RecordDescriptor(token, it));
		}
		else if(token.getTokenType().equals("StringIdentifier")){
			StringIdentifier temp = (StringIdentifier)token;
			symbolType = temp.getVarName();
			addChild(new Name(token));
			match("StringIdentifier");
		} else {
			addChild(new BasicType(token));
			switch(token.getTokenType()){
				case "byte" :
					symbolType = "byte";
					match("byte");
					break;
				case "int32":
					symbolType = "int32";
					match("int32");
					break;
				case "float64":
					symbolType = "float64";
					match("float64");
					break;
				default:
					break;
			}
		}
	}

	public String returnType() {
		return symbolType;
	}

	@Override 
	public void print(){
		printUp("+---");
		children.get(0).printUp(print);
		children.get(0).print();
	}
}

class RecordDescriptor extends Subtree{
	RecordDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		match("record");

		addChild(new FieldDeclarations(token, it));

		match("end");
	}

	@Override 
	public void print(){
		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " " + this.getClass());

		children.get(0).printUp(print);
		children.get(0).print();
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

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " list");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}

class FieldDeclaration extends Subtree{
	FieldDeclaration(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new Name(token));
		match("StringIdentifier");

		addChild(new TypeDescriptor(token, it));
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " Field Declaration");

		printUp("+---");

		System.out.println(print + "name");
		children.get(0).printUp(print);
		children.get(0).print(System.identityHashCode(this));

		System.out.println(print + "type");
		children.get(1).printUp(print);
		children.get(1).print();
	}
}

class Dimension extends Subtree{
	Dimension(Token t, Iterator<Token> i){
		super(t, i);

		match("OPEN_BRACKET");
		addChild(new Expressions(token, it));
		match("CLOSE_BRACKET");
	}

	@Override
	public void print(){
		children.get(0).printUp(print);
		children.get(0).print();
	}
}

class Params extends Subtree{
	Params(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new Param(token, it));

		this.symbol = new Symbol("Params");

		x();
	}

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException{
		Subtree currentNode = null;

		//decorateFirst and pass in the enclosing scope for the Param
		//to add itself to the enclosing scope's symbol table
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);
			currentNode.decorateFirst(enclosing);
		}
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

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
			+ System.identityHashCode(this) 
			+ " list");

		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp(print);
			children.get(i).print();
		}
	}
}

class Param extends Subtree{
	public boolean [] locks = new boolean[2]; //0-> ref, 1->const,

	Param(Token t, Iterator<Token> i){
		super(t, i);
		if(token.getTokenType().equals("ref")) {
			match("ref");
			locks[0] = true;
			if(token.getTokenType().equals("const")) {
				locks[1] = true;
				match("const");
				//this.symbol = new RefConstSymbol(token.getVal());
			} else {
				//this.symbol = RefSymbol(token.getVal());
			}
		}

		if(token.getTokenType().equals("const")) {
			match("const");
			locks[1] = true;
			//this.symbol = ConstSymbol(token.getVal());
		}

		addChild(new Name(token));
		match("StringIdentifier");

		if(token.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			addChild(new Expression(token, it));
		} else {

			addChild(new NaTypeDescriptor(token, it));

			if(hasWilds()){
				match("OPEN_BRACKET");
				addChild(new DimWilds(token, it));
				match("CLOSE_BRACKET");
			}
		}
	}

	/*
	 *	All variables must be pass by reference. All other parameters are pass by
	 *	value. If a formal parameter has a default value, all others must as well.
	 *	Decorate first should have ref, 
	 *
	 */

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		Subtree currentNode = null;
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);
			if(currentNode instanceof NaTypeDescriptor) {
				currentNode.decorateFirst(enclosing);
			}
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(" + row + ", "
			+ col + ") " 
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

			//skipping wilds for now
		}

		System.out.println(print + "initializer");
		if(children.get(1) instanceof Expression){
			children.get(1).printUp(print);
			children.get(1).print();
		}
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


/*------------------------------- Output Helpers ---------------------------------*/

// !-------------------------- NAME SHOULD BE FINISHED ----------------------------!

class Name extends Subtree{
	Name(Token t){
		token = t;
	}

	public String getName() {
		StringIdentifier temp = (StringIdentifier) token;
		return temp.getVarName();
	}

	@Override
	public void print(int a){
		printUp("+---");

		System.out.println(print + "(" + row + ", "
			+ col + ") "
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

		System.out.println(print + "(" + row + ", "
			+ col + ") "
			+ System.identityHashCode(token)
			+ " " + this.getClass() + ": "
			+ token.getTokenType());
	}
}

/*--------------------------------- Expressions ----------------------------------*/


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
		while(keepReading(token)) {
			Expression child = new Expression();
			child.populate(it, token);
			addChild(child);
			if(token.getTokenType().equals("COMMA")) {
				match("COMMA");
			}
		}
	}

	private boolean keepReading(Token current) {
		boolean keepReading = true;
		switch(current.getTokenType()) {
			case "CLOSE_PARENTHESIS":
				keepReading = false;
				break;
			case "SEMICOLON":
				keepReading = false;
				break;
			default:
				break;
		}
		return keepReading;
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ System.identityHashCode(this) 
			+ " expression");

		printUp("+---");

		for(int i = 0; i < children.size(); i++){
		 	children.get(i).printUp(print);
		 	children.get(i).print();
		}
	}
}

class Expression extends Subtree {
	Expression operand = null;
	String unaryOp = "";
	String tokenType = "";
	String tokenDescrip = "";
	double tokenNum;
	int precedence = 0;

	Expression() {}

	Expression(Token t, Iterator<Token> i){
		super(t, i);
		addAllChildren();
	}
	Expression(Token t, Iterator<Token> i, int p){
		super(t, i);
		precedence = p;
		addAllChildren();
	}

	public void setType(Scope enclosing){;}  //temporary workaround

	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException{
		//expressions type can be:
		//int32, float64
		//byte = char;

		//later on bc we dont have arrays implemented
		//byte[] = string;


		//temporary
		type = (BuiltInTypeSymbol)enclosing.resolve("int32");

		//not temporary
		symbol = new ExpressionSymbol(type);
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
		matchOperand();

		while(keepReading(token)) {
			int currPrec = precedence(token);
			if(currPrec > this.precedence) {
				this.precedence = currPrec;
				//System.out.println(precedence);
				Expression subexpression = new Expression(token, it,currPrec);
				addChild(subexpression);
			} else {
				ExprRest toAdd = new ExprRest(token, it);
				//System.out.println("back in Expression");
				addChild(toAdd);
			}
		}
	}

	public Iterator<Token> populate(Iterator<Token> i, Token current) {
		it = i;					//sets Node's iterator to i
		token = current;		//sets Node's token to current

		if(isUnary(token)) {				//checks if expression is unary
			match(token.getTokenType());	//moves the iterator forward
		}
		matchOperand();						//matches the operands

		//special cases (i.e. assignment, changes in operator precedence etc.)

		while(keepReading(token)) {
			ExprRest child = new ExprRest();
			it = child.populateExpr(it, token);
			addChild(child);
		}

		return it;
	}

	private boolean keepReading(Token current) {
		boolean isEndChar = true;
		switch(current.getTokenType()) {
			case "COMMA":
				isEndChar = false;
				break;
			case "CLOSE_PARENTHESIS":
				isEndChar = false;
				break;
			case "SEMICOLON":
				isEndChar = false;
				break;
			default:
				break;
		}
		return isEndChar;
	}

	private int precedence(Token tok) {
		int precedence = 0;
		switch(tok.getTokenType()) {
			case "PLUS":
				precedence = 10;
				break;
			case "MINUS":
				precedence = 10;
				break;
			case "ASTERISK":
				precedence = 11;
				break;
			case "BACKSLASH":
				precedence = 11;
				break;
			case "TILDE":
				precedence = 12;
				break;
			case "ASSIGNMENT_OPERATOR":
				precedence = 1;
				break;
			case "RELATIONAL_GREATER_THAN":
				precedence = 8;
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				precedence = 8;
				break;
			case "RELATIONAL_LESS_THAN":
				precedence = 8;
				break;
			case "RELATIONAL_LESS_EQUALTO":
				precedence = 8;
				break;
			case "BITWISE_AND":
				precedence = 6;
				break;
			case "LOGICAL_AND":
				precedence = 3;
				break;
			case "BITWISE_OR":
				precedence = 4;
				break;
			case "LOGICAL_OR":
				precedence = 2;
				break;
			case "EXCLAMATION_POINT":
				precedence = 12;
				break;
			case "BITWISE_XOR":
				precedence = 5;
				break;
			case "LOGICAL_NOT":
				precedence = 7;
				break;
			case "OUTPUT":
				precedence = 9;
				break;
			case "INPUT":
				precedence = 9;
				break;
			case "EQUALITY":
				precedence = 7;
				break;
			case "OPEN_PARENTHESIS":
				precedence = 13;
				break;
			case "CLOSE_PARENTHESIS":
				precedence = 0;
				break;
			default:
				break;
		}
		return precedence;
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ " expression");

		printUp("+---");

		String toPrint = print + this.toPrint();
		System.out.println(toPrint);
		toPrint = "";
		if(children != null) {
			for(int i = 0; i < children.size(); i++){
				//children.get(i).printUp("+---+---+---");
				toPrint += "" + children.get(i).toPrint() + "";
			}
			//System.out.println(toPrint);
		}
	}

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "IntIdentifier":
				//tokenType = Integer.toString(token.getVal());
				tokenDescrip = "Integer";
				match("IntIdentifier");
				break;
			case "FLOAT_IDENTIFIER":
				//tokenType = Float.toString(token.getVal());
				tokenDescrip = "Float";
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				//tokenType = Integer.toString(token.getVal());
				tokenDescrip = "Byte";
				match("BYTE_IDENTIFIER");
				break;
			case "StringIdentifier":
				tokenType = token.getName();
				tokenDescrip = "Identifier";
				match("StringIdentifier");
				break;
			case "KEYWORD_INT32":
				match("KEYWORD_INT32");
				match("OPEN_PARENTHESIS");
				tokenDescrip = "Keyword";
				addChild(new Expression(token, it));
				match("CLOSE_PARENTHESIS");
				break;
			case "KEYWORD_FLOAT":
				match("KEYWORD_FLOAT");
				match("OPEN_PARENTHESIS");
				tokenDescrip = "Keyword";
				addChild(new Expression(token, it));
				match("CLOSE_PARENTHESIS");
				break;
			case "KEYWORD_BYTE":
				match("KEYWORD_BYTE");
				tokenDescrip = "Keyword";
				match("OPEN_PARENTHESIS");
				addChild(new Expression(token, it));
				match("CLOSE_PARENTHESIS");
				break;
			case "OPEN_PARENTHESIS":
				match("OPEN_PARENTHESIS");
				tokenDescrip = "subexpression";
				tokenType = "";
				Expression child = new Expression();
				it = child.populate(it, token);
				addChild(child);
				match("CLOSE_PARENTHESIS");
				break;
			case "String_Literal":
				tokenType = token.getName();
				tokenDescrip = "String Literal";
				match("String_Literal");
				break;
			case "Char_Literal":
				match("Char_Literal");
				tokenDescrip = "Char Literal";
				break;
			default:
				break;
		}
	}
	private boolean isUnary(Token current) {
		boolean isUnary = false;
		switch(current.getTokenType()) {
			case "TILDE":
				isUnary = true;
				unaryOp = "~";
				break;
			case "EXCLAMATION_POINT":
				isUnary = true;
				unaryOp = "!";
				break;
			case "MINUS":
				unaryOp = "-";
				isUnary = true;
				break;
			default:
				break;
		}
		return isUnary;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		
		retVal += "" + tokenType + " ";

		if(children != null) {
			for(int index = 0; index < children.size(); index++) {
				retVal += children.get(index).toPrint();
			}
		}

		return retVal;
	}

}

class ExprRest extends Subtree {
	public Token operator = null;		//holds operator
	public Token unaryOp = null;
	public Token operand = null;		//holds operand
	public String uOperator = "";
	public String opType = "";			//descriptor for operation type
	public String operandType = "";		//descriptor for operand
	int precedence = 0;		

	ExprRest() {}

	ExprRest(int p) {
		precedence = p;
	}

	ExprRest(Token t, Iterator<Token> i){
		super(t, i);
		addAllChildren();
	}
	ExprRest(Token t, Iterator<Token> i, int p){
		super(t, i);
		precedence = p;
		addAllChildren();
	}

	/*	Checks first to see if the operand is a valid built-in type. The only types which are included
	 *	in operations are int32, byte, and float64. We know since we are in the ExprRest node that an
	 *	operation of some type is going to be performed. Second step is to check whether the operation
	 *	is valid for the given symboltype provided by the first step.
	 */

	public void decorateExpr(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		//first thing to do is check the type of the operand and if it is an int, float or byte, we are good.
		//otherwise we need to resolve the type in the enclosing scope or throw an error


	}

	public Iterator<Token> populateExpr(Iterator<Token> i, Token current) {
		it = i;
		token = current;
		if(!isEndChar(token)) {
			return it;
		}

		populate();
		return it;
	}

	private void addAllChildren() {
		if(keepReading(token)) {
			populate();

			if(keepReading(token)) {
				ExprRest child = new ExprRest();
				it = child.populateExpr(it,token);
				addChild(child);
			}
		}
	}

	private boolean isEndChar(Token current) {
		boolean isEndChar = true;
		switch(current.getTokenType()) {
			case "COMMA":
				isEndChar = false;
				break;
			case "CLOSE_PARENTHESIS":
				isEndChar = false;
				break;
			case "SEMICOLON":
				isEndChar = false;
				break;
			default:
				break;
		}
		return isEndChar;
	}

	/*
	 *	Method creates a subexpression if an open paranthesis is the current token.
	 *	Otherwise the next operator and operand are read from the expression
	 *	statement.
	 *
	 */
	private int precedence(Token tok) {
		int precedence = 0;
		switch(tok.getTokenType()) {
			case "PLUS":
				precedence = 10;
				break;
			case "MINUS":
				precedence = 10;
				break;
			case "ASTERISK":
				precedence = 11;
				break;
			case "BACKSLASH":
				precedence = 11;
				break;
			case "TILDE":
				precedence = 12;
				break;
			case "ASSIGNMENT_OPERATOR":
				precedence = 1;
				break;
			case "RELATIONAL_GREATER_THAN":
				precedence = 8;
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				precedence = 8;
				break;
			case "RELATIONAL_LESS_THAN":
				precedence = 8;
				break;
			case "RELATIONAL_LESS_EQUALTO":
				precedence = 8;
				break;
			case "BITWISE_AND":
				precedence = 6;
				break;
			case "LOGICAL_AND":
				precedence = 3;
				break;
			case "BITWISE_OR":
				precedence = 4;
				break;
			case "LOGICAL_OR":
				precedence = 2;
				break;
			case "EXCLAMATION_POINT":
				precedence = 12;
				break;
			case "BITWISE_XOR":
				precedence = 5;
				break;
			case "LOGICAL_NOT":
				precedence = 7;
				break;
			case "OUTPUT":
				precedence = 9;
				break;
			case "INPUT":
				precedence = 9;
				break;
			case "EQUALITY":
				precedence = 7;
				break;
			case "OPEN_PARENTHESIS":
				precedence = 13;
				break;
			case "CLOSE_PARENTHESIS":
				precedence = 0;
				break;
			default:
				break;
		}
		return precedence;
	}
	private void populate() {
		matchOperator();
		if(isUnary(token)) {
			matchOperator();
		}
		matchOperand();
	}

	private boolean keepReading(Token current) {
		boolean isEndChar = true;
		switch(current.getTokenType()) {
			case "COMMA":
				isEndChar = false;
				break;
			case "CLOSE_PARENTHESIS":
				isEndChar = false;
				break;
			case "SEMICOLON":
				isEndChar = false;
				break;
			default:
				break;
		}
		return isEndChar;
	}

	/*
	 *	Matches the operator in the expression statement. Function sets the 
	 *	operator to the current token and opType to the name of the current
	 *	token for later printing.
	 *
	 */

	private void matchOperator() {
		switch(token.getTokenType()) {
			case "PLUS":
				operator = token;
				opType = "+";
				match("PLUS");
				break;
			case "MINUS":
				operator = token;
				opType = "-";
				match("MINUS");
				break;
			case "ASTERISK":
				operator = token;
				opType = "*";
				match("ASTERISK");
				break;
			case "BACKSLASH":
				operator = token;
				opType = "/";
				match("BACKSLASH");
				break;
			case "TILDE":
				operator = token;
				opType = "~";
				match("TILDE");
				break;
			case "ASSIGNMENT_OPERATOR":
				operator = token;
				opType = "=";
				match("ASSIGNMENT_OPERATOR");
				break;
			case "RELATIONAL_GREATER_THAN":
				operator = token;
				opType = ">";
				match("RELATIONAL_GREATER_THAN");
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				operator = token;
				opType = ">=";
				match("RELATIONAL_GREATER_EQUALTO");
				break;
			case "RELATIONAL_LESS_THAN":
				operator = token;
				opType = "<";
				match("RELATIONAL_LESS_THAN");
				break;
			case "RELATIONAL_LESS_EQUALTO":
				operator = token;
				opType = "<=";
				match("RELATIONAL_LESS_EQUALTO");
				break;
			case "BITWISE_AND":
				operator = token;
				opType = "&";
				match("BITWISE_AND");
				break;
			case "LOGICAL_AND":
				operator = token;
				opType = "&&";
				match("LOGICAL_AND");
				break;
			case "BITWISE_OR":
				operator = token;
				opType = "|";
				match("BITWISE_OR");
				break;
			case "LOGICAL_OR":
				operator = token;
				opType = "||";
				match("LOGICAL_OR");
				break;
			case "EXCLAMATION_POINT":
				operator = token;
				opType = "!";
				match("EXCLAMATION_POINT");
				break;
			case "BITWISE_XOR":
				operator = token;
				opType = "^";
				match("BITWISE_XOR");
				break;
			case "LOGICAL_NOT":
				operator = token;
				opType = "!=";
				match("LOGICAL_NOT");
				break;
			case "OUTPUT":
				operator = token;
				opType = "<<";
				match("OUTPUT");
				break;
			case "INPUT":
				operator = token;
				opType = ">>";
				match("INPUT");
				break;
			case "EQUALITY":
				operator = token;
				opType = "==";
				match("EQUALITY");
				break;
			default:
				break;
		}
	}

	private boolean isUnary(Token current) {
		boolean isUnary = false;
		switch(current.getTokenType()) {
			case "TILDE":
				uOperator = "~";
				isUnary = true;
				break;
			case "EXCLAMATION_POINT":
				uOperator = "!";
				isUnary = true;
				break;
			case "MINUS":
				uOperator = "-";
				isUnary = true;
				break;
			default:
				break;
		}
		return isUnary;
	}

	/*
	 *	Matches the operand in the expression statement. matchOperator() must be called
	 *	before matchOperand is called. Function sets the operand to the current token
	 *	and operandType to the name of the token for later printing.
	 *
	 */

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "IntIdentifier":
				operand = token;
				operandType = Integer.toString(((IntIdentifier)token).getVal());
				match("IntIdentifier");
				break;
			case "FLOAT_IDENTIFIER":
				operand = token;
				operandType = Float.toString(((FloatIdentifier)token).getVal());
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				operand = token;
				operandType = Integer.toString(((IntIdentifier)token).getVal());
				match("BYTE_IDENTIFIER");
				break;
			case "StringIdentifier":
				operand = token;
				//operandType = "StringIdentifier";
				operandType = token.getName();
				match("StringIdentifier");
				break;
			case "KEYWORD_INT32":
				operand = token;
				operandType = "Keyword int32";
				//operandType = "INT32";
				match("KEYWORD_INT32");
				break;
			case "KEYWORD_FLOAT":
				operand = token;
				operandType = "Keyword float";
				//operandType = "FLOAT";
				match("KEYWORD_FLOAT");
				break;
			case "KEYWORD_BYTE":
				operand = token;
				operandType = "Keyword byte";
				//operandType = "BYTE";
				match("KEYWORD_BYTE");
				break;
			default:
				break;
		}
	}

	/*
	 *	Function overriden from parent class. Prints the information
	 *	for the current node as well as any of then node's children.
	 *
	 */

	@Override
	public void print(){
		if(operand == null) {
			return;
		}
		printUp("+---");
		System.out.print(print + "" + opType + " " + uOperator + operandType + "");
	}

	@Override
	public String toPrint() {
		String retVal = "" + opType + " " + uOperator + operandType + "";

		if(children != null) {
			for(int index = 0; index < children.size(); index++) {
				if(children.get(index) instanceof Expression){
					retVal += "(" + children.get(index).toPrint() + ")";
				} else {
					retVal += children.get(index).toPrint();
				}
			}
		}

		return retVal;
	}
		
}