import java.io.*;
import java.util.*;

/*
 *	Look at typeCheck function defined in Subtree and Block class.
 *	Also, should we think about having the Node class's keep track of their
 *	Type? Is resolve the same as lookUp? I am assuming so. I think we will
 *	need a symbol type for any definition type nodes, such as function
 *	definitions (declarations), variable declarations etc. The first pass
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

	public void decor2(SymbolTable mainTable){;}

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

	Function(Token t, Iterator<Token> i) {
		super(t, i);								//calls super constructor
		match("function");							//matches type
		//creates new FunctionSymbol object unique to this node
		//first child is now Name node
		addChild(new Name(token));
		match("StringIdentifier");					//increments Iterator
		match("OPEN_PARENTHESIS");					//increments Iterator, enter Params

		if(hasParams()) {							//checks if Params are present
			addChild(new Params(token, it));		//adds child Nodes accordingly
		}

		match("CLOSE_PARENTHESIS");					//increments Iterator

		if(hasTypeDesc()) {							//checks for type descriptor
			addChild(new TypeDescriptor(token, it));//adds child node accordingly
		}

		addChild(new Block(token, it));				//final child, Block
	}

	/*
	 *	Semantic Rules for Parameters:
	 *	1. All parameters of type var must be pass by reference
	 *	2. All parameters not of type var must be pass-by-value
	 *	3. If a formal parameter has a default value, all others must as well
	 *
	 *	We want each Function Symbol to hold the id of the function, checking that
	 *	it has not already been defined in the scope. And to hold the return
	 *	type of the function, if there is one.
	 *	
	 *	We want each parameter to return whether or not it is a var (if so it must
	 *	be pass by reference), and if the value is initialized. This will be denoted
	 *	by whether or not the pattern id value is followed.
	 *
	 */

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException {
		//Symbol to check if the name of this function has already been used in
		//the parent scope. If so throws an AlreadyDefinedException
		Symbol previouslyDefined = enclosing.resolve(token.getTokenType());
		if(previouslyDefined != null) {	//name has already been used
			throw new AlreadyDefinedException(token.getTokenType());
		} else {
			//otherwise, create scope for new function
			LocalScope functionScope = new LocalScope(enclosing);
			Subtree currentNode;
			String typeDescrip = "";
			//iterate over children until we find the typedescriptor (if it has one)
			for(int index = 0; index < children.size(); index++) {
				currentNode = children.get(index);
				//if the function has a type descriptor (return type), then we want
				//to call the corresponding function on that node that will return
				//what the actual descriptor is so we can add it to the Symbol for
				//this function. We want Symbol = (function id name, return type)
				if(currentNode instanceof TypeDescriptor) {
					//now have String representation of return type. This now
					//needs to be resolved to check that it is a valid return type
					//that has already been defined in the scope of the program
					//thus far
					TypeDescriptor tempDescrip = (TypeDescriptor) currentNode;
					typeDescrip = tempDescrip.returnType();
					//if the typeDescrip has been previously defined then previouslyDefined
					//should not equal null
					previouslyDefined = enclosing.resolve(typeDescrip);
					if(previouslyDefined == null) {
						throw new UndefinedTypeException(typeDescrip);
					} else {
						SymbolType symT = (SymbolType) previouslyDefined;
						Name name = (Name) children.get(0);
						symbol = new FunctionSymbol(name.getName(), symT);
						enclosing.define(symbol);
					}
				}
			}
		}
	}

	/*
	 *	Second decorate function. The Function symbol has already been added to the
	 *	enclosing scope. The encompassing scope is passed in to decorate second and
	 *	a local scope is created. Parameters are added to the local scope, and then
	 *	the local scope is passed to the Block child, as the Block child requires
	 *	the creation of a new semantic scope.
	 *
	 */

	@Override
	public void decorateSecond(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		//creates scope local to the function
		LocalScope localScope = new LocalScope(enclosing);

		//iterates over children. When a child of type Params is encountered
		//the decorateSecond() function is called on that child and the localScope
		//is passed in so that the parameters can be added to the function's scope
		Subtree currentNode;
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);
			if(currentNode instanceof Params) {
				//adds Params to localScope symbol table
				currentNode.decorateSecond(localScope);
			} else if(currentNode instanceof Block) {
				//passes local scope to Block, local scope will become the Block's
				//encompassing scope
				Block blockNode = (Block) currentNode;
				blockNode.decorateBlock(localScope);
			}
		}
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

	public Var(Token t, Iterator<Token> i){
		super(t, i);

		if(token.getTokenType().equals("static"))
			match("static");
		
		if(token.getTokenType().equals("const"))
			match("const");

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
	 *	Want to store the identifier and the type-desciptor in the first
	 *	pass if the rule:
	 *	variable-decl -> static(opt) const(opt) var identifier type-descriptor
	 *
	 *	or we want to store the identifier and the type descriptor of the
	 *	expression, as the initializing will be done in the second pass.
	 *
	 */

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		Symbol previouslyDefined = enclosing.resolve(children.get(0).token.getName());

		if(previouslyDefined != null){
			throw new AlreadyDefinedException(previouslyDefined.getName());
		} else {
			if(children.get(1) instanceof TypeDescriptor){
				Subtree nodeType = children.get(1).children.get(0).children.get(0);

				if(nodeType instanceof BasicType) {
					SymbolType t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
					symbol = new VarSymbol(children.get(0).token.getName(), t);
					enclosing.define(symbol);
				}
				else if(nodeType instanceof Name){
					//type zoeller byte;
					//type john zoeller; -> resolve zoeller's type, thus john => byte
					//var fred john;     -> resolve john's type, thus fred => byte

					//Symbol tempS = enclosing.resolve(nodeType.token.name());
					//SymbolType t = (BuiltInTypeSymbol)tempS.getType();
					SymbolType t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getName()).getType();
					symbol = new VarSymbol(children.get(0).token.getName(), t);
					enclosing.define(symbol);

				}
				else if(nodeType instanceof RecordDescriptor){
					// i can't decide what to do with recordDescriptor
					// i think since its properties are predefined it should be 
					// a builtInType

					// but then again, builtInTypes should be able to go like this
					// var john int32; var dest int32; var aaa = john + dest; 
					//
					// a record is incapable of this behavior 
					// var john record (a byte, b float64);
					// var fred record (c byte, d byte);
					//
					// these are both records, but obviously trying to go
					// var zoeller = john + fred; 
					// would not work

					// Conclusion:
					// record should have its own decorateFirst() method,
					// this way a var/type could be decorated with a record node
					// 
					//S
				}
			}
		}		
	}


	/*
	 *	For variables the most important thing is that the expression that
	 *	the variable is assigned to matches the variable's type descriptor.
	 *
	 */

	public void decorateSecond(Scope enclosing) {

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

	/*	Semantic Rules for Type Declarations:
	 *
	 *	1. type-decl -> type id type-descriptor
	 *	2. type-descriptor -> basic type, id, record descriptor
	 *	3. Want the symbol for type to be the identifier for the type, and the return type
	 *	   the return type must be an already declared type within the enclosing scope
	 *
	 */

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException {
		//First step is to check that the identifier for this type-declaration is not already
		//defined in the enclosing scope
		Name nameNode = (Name) children.get(0);
		String typeName = nameNode.getName();

		Symbol previouslyDefined = enclosing.resolve(typeName);
		if(previouslyDefined != null) {		//type name has already been used
			throw new AlreadyDefinedException(typeName);
		} else {
			//we know that the type identifier is unique within the scope and available
			//for use. check if the type-descriptor HASbeen previously defined.
			
			TypeDescriptor typeDescriptor = (TypeDescriptor) children.get(1);
			String typeDescription = typeDescriptor.returnType();
			previouslyDefined = enclosing.resolve(typeDescription);

			if(previouslyDefined == null) {
				throw new UndefinedTypeException(typeDescription);
			} else {
				//Since both of these things check out, we can add the new type-declaration
				//to the enclosing scope
				SymbolType symT = (SymbolType) previouslyDefined;
				symbol = new VarSymbol(typeName, symT);
				enclosing.define(symbol);
			}
		}
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

	public void decorateBlock(Scope encompassing) throws UndefinedTypeException, AlreadyDefinedException {
		//creates scope local to the block
		LocalScope localScope = new LocalScope(encompassing);

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
		//iterate over children and if the node is a Param, we will call
		//decorateFirst and pass in the enclosing scope for the Param
		//to add itself to the enclosing scope's symbol table
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);
			if(currentNode instanceof Param) {
				currentNode.decorateFirst(enclosing);
			}
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
	Param(Token t, Iterator<Token> i){
		super(t, i);
		if(token.getTokenType().equals("ref")) {
			match("ref");
			if(token.getTokenType().equals("const")) {
				match("const");
				//this.symbol = new RefConstSymbol(token.getVal());
			} else {
				//this.symbol = RefSymbol(token.getVal());
			}
		}

		if(token.getTokenType().equals("const")) {
			match("const");
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
			+ " expression statement ");

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
		tokenType = t.getTokenType();
		addAllChildren();
	}
	Expression(Token t, Iterator<Token> i, int p){
		super(t, i);
		tokenType = t.getTokenType();
		precedence = p;
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
		matchOperand();

		while(keepReading(token)) {
			int currPrec = precedence(token);
			if(currPrec > this.precedence) {
				this.precedence = currPrec;
				System.out.println(precedence);
				Expression subexpression = new Expression(token, it,currPrec);
				addChild(subexpression);
			} else {
				ExprRest toAdd = new ExprRest(token, it);
				System.out.println("back in Expression");
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
		for(int i = 0; i < children.size(); i++){
			children.get(i).printUp("+---+---+---");
			children.get(i).print();
		}
	}

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "IntIdentifier":
				tokenType = Integer.toString(token.getVal());
				tokenDescrip = "Integer";
				match("IntIdentifier");
				break;
			case "FLOAT_IDENTIFIER":
				tokenType = Float.toString(token.getVal());
				tokenDescrip = "Float";
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				tokenType = Integer.toString(token.getVal());
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
		
		retVal += " " + tokenType + " " + tokenDescrip + " ";

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

	/*
	 *	Constructor. Uses Subtree super class.
	 *
	 */

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
				operandType = Integer.toString(token.getVal());
				match("IntIdentifier");
				break;
			case "FLOAT_IDENTIFIER":
				operand = token;
				//operandType = "Identifier";
				operandType = Float.toString(token.getVal());
				match("FLOAT_IDENTIFIER");
				break;
			case "BYTE_IDENTIFIER":
				operand = token;
				//operandType = "Identifier";
				operandType = Integer.toString(token.getVal());
				match("BYTE_IDENTIFIER");
				break;
			case "StringIdentifier":
				operand = token;
				//operandType = "Identifier";
				operandType = token.getName();
				match("StringIdentifier");
				break;
			case "KEYWORD_INT32":
				operand = token;
				//operandType = "Keyword";
				operandType = "INT32";
				match("KEYWORD_INT32");
				break;
			case "KEYWORD_FLOAT":
				operand = token;
				//operandType = "Keyword";
				operandType = "FLOAT";
				match("KEYWORD_FLOAT");
				break;
			case "KEYWORD_BYTE":
				operand = token;
				//operandType = "Keyword";
				operandType = "BYTE";
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
		System.out.print(this.toPrint());
	}

	@Override
	public String toPrint() {
		String retVal = "";
		
		retVal = " " + opType + " " + uOperator + operandType + "";

		return retVal;
	}
		
}