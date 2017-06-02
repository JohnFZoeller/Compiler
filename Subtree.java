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

	String defaultInt = "-2147483648", defaultFloat = "-123456789.123456789";

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

	public Subtree(Subtree toCopy) {
		this.token = new Token(toCopy.token);
		this.it = toCopy.it;
	}

	public SymbolType getSymType(){
		return type;
		//collapse
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

	public void decorateFirst(Scope e) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {;}

	public void decorateSecond(Scope e) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {;}

	public void beginDecorateFirst(SymbolTable mainTable) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = mainTable.globals;

		for(int i = 0; i < children.size(); i++){
			children.get(i).decorateFirst(currentScope);
		}
	}

	public void beginDecorateSecond(SymbolTable mainTable)throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = mainTable.globals;

		for(int i = 0; i < children.size(); i++){
			 children.get(i).decorateSecond(null);
		}
	}

	public void emitAssemblyCode(List<String> consts){
		System.out.println("ASSEMBLY CODE \n");

		for(int i = 0; i < children.size(); i++){
			children.get(i).emit(consts);
		}

		System.out.println("\n");
		printConstants(consts);
	}

	public void printConstants(List<String> consts){

		for(int i = 0; i < consts.size(); i++){
			System.out.println(consts.get(i));
		}
	}

	public void emit(List<String> c){}

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

	//this eventually needs to be changed to an actual condition
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

	public void setType(Scope e) throws UndefinedTypeException {;}  //temporary workaround

	public Symbol makeArray(String n, SymbolType t, boolean [] l, int s, RecordSymbol r){
		ArraySymbol arr = (r == null) ? new ArraySymbol(n, s, t) : new ArraySymbol(n, s, t, r);
		SymbolType typ = (ArraySymbol)currentScope.resolve("array");

		if(this instanceof Function) 
			return new FunctionSymbol(n, typ, currentScope, arr);
		//else if(this instanceof Var) return new VarS....
		if(l != null){
			return new VarSymbol(n, typ, l, arr);
		}
		else{
			return new TypeSymbol(n, typ, arr);
		}
	}

	public Symbol makeRecord(String n, boolean [] locs, Scope cur, List<Subtree> fds){
		RecordSymbol rec = new RecordSymbol(n, cur, fds);
		type = (RecordSymbol)cur.resolve("record");

		if(locs != null)
			return new VarSymbol(n, type, rec, locs);
		else return new TypeSymbol(n, type, rec);
	}

}

/*------------------------------ Main Node Types ---------------------------------*/
/*------------------------------Nodes with Blocks---------------------------------*/

class For extends Subtree{
	Block block;

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
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = new LocalScope(enclosing);
		block = (Block)children.get(3);
		block.decorateFirst(currentScope);
		block.decorateSecond(currentScope);
		currentScope = currentScope.getEnclosingScope();
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
	Block block;

	While(Token t, Iterator<Token> i){
		super(t, i);

		match("while");
		match("OPEN_PARENTHESIS");

		addChild(new Expression(token, it));

		match("CLOSE_PARENTHESIS");

		addChild(new Block(token, it));
	}

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = new LocalScope(enclosing);
		block = (Block)children.get(1);
		block.decorateFirst(currentScope);
		block.decorateSecond(currentScope);
		currentScope = currentScope.getEnclosingScope();
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
	Block block;

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
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = new LocalScope(enclosing);
		block = (Block)children.get(1);
		block.decorateFirst(currentScope);
		block.decorateSecond(currentScope);
		currentScope = currentScope.getEnclosingScope();
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
	Block block;

	Else(Token t, Iterator<Token> i){
		super(t, i);
		match("else");
		addChild(new Block(token, it));
	}

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		currentScope = new LocalScope(enclosing);
		block = (Block)children.get(0);
		block.decorateFirst(currentScope);
		block.decorateSecond(currentScope);
		currentScope = currentScope.getEnclosingScope();
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

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		String funcName = children.get(0).token.getName();
		Symbol previouslyDefined = enclosing.resolve(funcName);
		Subtree currentNode;
		String symType;
		currentScope = enclosing;
		boolean isArray = false;

		if(previouslyDefined != null) {
			throw new AlreadyDefinedException(token.getTokenType());
		} else {
			if(!flags[1]) {														//no type
				type = (BuiltInTypeSymbol)enclosing.resolve("void");			//type = void
				currentNode = (flags[0]) ? children.get(1) : null;				//cur = PARAMS HERE
			} else {															//has a type
				currentNode = (flags[0]) ? children.get(2) : children.get(1);	//cur = TYPE HERE
				Subtree nodeType = currentNode.children.get(0).children.get(0);	//nodeType
				isArray = ((TypeDescriptor)currentNode).array;					//array?

				if(nodeType instanceof BasicType){
					type = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
					symbol = (isArray) ? (FunctionSymbol)makeArray(funcName, type, null, 4, null) :
						new FunctionSymbol(funcName, type, enclosing);
				}
				else if(nodeType instanceof Name){
					previouslyDefined = enclosing.resolve(nodeType.token.getName());

					if(previouslyDefined == null || !previouslyDefined.getTypeSymbol())
						throw new UndefinedTypeException(((Name)nodeType).token.getName());
					else{
						symType = previouslyDefined.getType().getTypeName();

						if(symType == "array"){
							type = (ArraySymbol)previouslyDefined.getType();
						} else if(symType == "record"){
							type = (RecordSymbol)previouslyDefined.getType();
						} else {
							type = (BuiltInTypeSymbol)previouslyDefined.getType();
						}

						symbol = (isArray) ? (FunctionSymbol)makeArray(funcName, type, null, 4, null) : 
							new FunctionSymbol(funcName, type, enclosing);
					}
				}
				else if(nodeType instanceof RecordDescriptor){
					RecordSymbol rec = new RecordSymbol(funcName, enclosing, nodeType.children.get(0).children);
					type = (RecordSymbol)enclosing.resolve("record");

					symbol = (isArray) ? (FunctionSymbol)makeArray(funcName, type, null, 4, rec) : 
						new FunctionSymbol(funcName, type, enclosing, rec);
				}
			}

			if(flags[0])
				symbol.addParams(children.get(1).children);

			currentScope.define(symbol);
		}
	}

	@Override
	public void decorateSecond(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		localScope = new LocalScope((FunctionSymbol)symbol);

		if(flags[0] && flags[1])
			block = (Block)children.get(3);
		else if((flags[0] && !flags[1]) || (!flags[0] && flags[1]))
			block = (Block)children.get(2);
		else block = (Block)children.get(1);

		block.decorateFirst(localScope);
		block.decorateSecond(localScope);

		currentScope = ((FunctionSymbol)symbol).getEnclosingScope();
	}

	@Override
	public void emit(List<String> consts){
		String instruction = children.get(0).token.getName() + ":\n\t";
		String symType = symbol.getType().getTypeName();

		if(symType == "int32" || symType == "byte"){
				instruction += "int_literal " + defaultInt;
		} else {
				instruction += "float_literal " + defaultFloat;
		}

		consts.add(instruction);
		block.emit(consts);
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

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		String symbolName = children.get(0).token.getName();
		Symbol previouslyDefined = enclosing.resolve(symbolName);
		String symType;
		currentScope = enclosing;

		if(previouslyDefined != null){
			throw new AlreadyDefinedException(previouslyDefined.getName());
		} else {
			if(children.get(1) instanceof TypeDescriptor){
				Subtree nodeType = children.get(1).children.get(0).children.get(0);
				boolean isArray = ((TypeDescriptor)children.get(1)).array;

				if(nodeType instanceof BasicType){
					type = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
					symbol = (isArray) ? (VarSymbol)makeArray(symbolName, type, locks, 4, null) :
						new VarSymbol(symbolName, type, locks);
				}
				else if(nodeType instanceof Name){//fix me to include records
					previouslyDefined = enclosing.resolve(nodeType.token.getName());

					if(previouslyDefined == null || !previouslyDefined.getTypeSymbol())
						throw new UndefinedTypeException(((Name)nodeType).token.getName());
					else{
						symType = previouslyDefined.getType().getTypeName();

						if(symType == "array"){
							type = (ArraySymbol)previouslyDefined.getType();
						} else if(symType == "record"){
							type = (RecordSymbol)previouslyDefined.getType();
						} else {
							type = (BuiltInTypeSymbol)previouslyDefined.getType();
						}

						symbol = (isArray) ? (VarSymbol)makeArray(symbolName, type, locks, 4, null) : 
							new VarSymbol(symbolName, type, locks);
					}
				}
				else if(nodeType instanceof RecordDescriptor){
					RecordSymbol rec = new RecordSymbol(symbolName, enclosing, nodeType.children.get(0).children);
					type = (RecordSymbol)enclosing.resolve("record");

					symbol = (isArray) ? (VarSymbol)makeArray(symbolName, type, locks, 4, rec) : 
						new VarSymbol(symbolName, type, rec, locks);
					
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

	@Override
	public void emit(List<String> consts){
		Subtree emitType = children.get(1);
		String instruction = children.get(0).token.getName() + ":\n\t";
		String symType = symbol.getType().getTypeName();
		if(emitType instanceof Expression){
			//getExpressionType();
			//instruction += expressionType + default<>
			;
		} else {
			if(symType == "int32" || symType == "byte"){
				instruction += "int_literal " + defaultInt;
			} else {
				instruction += "float_literal " + defaultFloat;
			}
			if(symType == "array"){
				//if(array type is an int)
				instruction += "int_literal " + defaultInt;
				consts.add(instruction);
				instruction = "arraySize: \n\t int_literal " + "2";
				System.out.println("load_label " + "arraySize");
				System.out.println("load_mem_int \nalloc_int");
			}
			//System.out.println(instruction);

		}
		consts.add(instruction);
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
		//System.out.println(token.getName());
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

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		String symbolName = children.get(0).token.getName();
		String symType;
		Symbol previouslyDefined = enclosing.resolve(symbolName);
		currentScope = enclosing;

		if(previouslyDefined != null){
			throw new AlreadyDefinedException(previouslyDefined.getName());
		} else {
			Subtree nodeType = children.get(1).children.get(0).children.get(0);
			boolean isArray = ((TypeDescriptor)children.get(1)).array;

			if(nodeType instanceof BasicType){
				type = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
				symbol = (isArray) ? (TypeSymbol)makeArray(symbolName, type, null, 4, null) :
					new TypeSymbol(symbolName, type);
			}
			else if(nodeType instanceof Name){
				previouslyDefined = enclosing.resolve(nodeType.token.getName());

				if(previouslyDefined == null || !previouslyDefined.getTypeSymbol())
					throw new UndefinedTypeException(children.get(1).token.getName());
				else{
					symType = previouslyDefined.getType().getTypeName();

					if(symType == "array"){
						type = (ArraySymbol)previouslyDefined.getType();
					} else if(symType == "record"){
						type = (RecordSymbol)previouslyDefined.getType();
						//add dot stuff here
						//need to keep resolving other children if there are some
					} else {
						type = (BuiltInTypeSymbol)previouslyDefined.getType();
					}

					symbol = (isArray) ? (TypeSymbol)makeArray(symbolName, type, null, 4, null) : 
						new TypeSymbol(symbolName, type);
				}
			}
			else if(nodeType instanceof RecordDescriptor){
				RecordSymbol rec = new RecordSymbol(symbolName, enclosing, nodeType.children.get(0).children);
				type = (RecordSymbol)enclosing.resolve("record");

				symbol = (isArray) ? (TypeSymbol)makeArray(symbolName, type, null, 4, rec) : 
					new TypeSymbol(symbolName, type, rec);
			}
			enclosing.define(symbol); 
		}	
	}

	//empty operation 
	public void decorateSecond(Scope cur) throws UndefinedTypeException, AlreadyDefinedException {
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
	public void decorateFirst(Scope local) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		currentScope = local;

		for(int index = 0; index < children.size(); index++) {
			children.get(index).decorateFirst(local);
		}
	}

	@Override
	public void decorateSecond(Scope local) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
		currentScope = local;

		for(int index = 0; index < children.size(); index++) {
			children.get(index).decorateSecond(local);
		}
	}

	@Override
	public void emit(List<String> consts){

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
	boolean array = false;

	TypeDescriptor(Token t, Iterator<Token> i){
		super(t, i);

		addChild(new NaTypeDescriptor(token, it));

		if(isDimensh()){
			addChild(new Dimension(token, it));	
			array = true;
		}
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

			while(token.getTokenType().equals("DOT")){
				match("DOT");
				addChild(new Name(token));
				match("StringIdentifier");
			}

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

	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, AlreadyDefinedException, IllegalOperationException {
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
	public boolean [] locks = new boolean[3]; //0-> ref, 1->const, 2-> wilds

	Param(Token t, Iterator<Token> i){
		super(t, i);
		
		if(token.getTokenType().equals("ref")) {
			match("ref");
			locks[0] = true;
		}

		if(token.getTokenType().equals("const")) {
			match("const");
			locks[1] = true;
		}

		addChild(new Name(token));
		match("StringIdentifier");

		if(token.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			addChild(new Expression(token, it));
		} else {

			addChild(new NaTypeDescriptor(token, it));

			if(hasWilds()){
				locks[2] = true;
				match("OPEN_BRACKET");
				addChild(new DimWilds(token, it));
				match("CLOSE_BRACKET");
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
			Expression child = new Expression(token, it);
			addChild(child);
			if(token.getTokenType().equals("COMMA")) {
				match("COMMA");
			}
		}
	}

	//had to add the closebracket case to make arrays work.
	//technically i think its incorrect tho... bc for something like this
	//var array int32[4];
	//array[2] = 4;
	//var john = array[2] + 6;
	//it would parse incorrectly
	//then again... i dont know expressions so well, so maybe it does work 
	private boolean keepReading(Token current) {
		boolean keepReading = true;
		switch(current.getTokenType()) {
			case "CLOSE_PARENTHESIS":
				keepReading = false;
				break;
			case "SEMICOLON":
				keepReading = false;
				break;
			case "CLOSE_BRACKET":
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

	Expression(Token t, Iterator<Token> i){
		super(t, i);
		this.token = t;
		this.it = i;
		readExpression();
	}

	Expression(Subtree leftHand, Subtree operator, Token t, Iterator<Token> i) {
		super(t, i);
		addOperandChild(new Token(leftHand.token), true);
		addOperatorChild(new Token(operator.token), true);
		this.token = t;
		this.it = i;
		if(continueReading())
			readExpression();
	}

	/*
		A const-expression is an expression whose operands are literals and/or variables declared
		const and are initialized by const-expressions.

		A compile-time error shall be issued if the operand to a bitwise or boolean operator is of
		floating-point type.

		Usual math operations can be performed on int32 and float64

		The binary operator, except assignment, where one of the operands is an integer type and the
		other is floating-type should automatically convert the int to float

		No-type casting for non-primitive types
	*/

	@Override
	public void decorateFirst(Scope enclosing) throws UndefinedTypeException, IllegalOperationException, AlreadyDefinedException {
		//first step is going to be to check that operands and operators are appropriate for the
		//type of expression. This includes resolving the types of different identifiers.

		//iterate through the children. When we find an operator we will want to compare the
		//left and right hand sides of the expression to make sure that it is a valid operation
		//for the given types.

		//if the operand is of type identifier or function-call then we will need to call
		//decorateFirst on them to resolve their types before comparing them to the other
		//side of the expression

		//first test-case var x = 6 + 2
		//second test-case var x = s + 2
		System.out.println("decorateFirst in Expression called");
		//System.out.println("Character test" + Character.toString((Character)'c'));

		Subtree currentNode = null;
		Subtree lhs = null;
		Subtree rhs = null;
		Subtree op = null;
		for(int index = 0; index < children.size(); index++) {
			currentNode = children.get(index);

			if(currentNode instanceof MathOp) {
				lhs = children.get(index - 1);
				rhs = children.get(index + 1);

				validate(lhs, currentNode, rhs, enclosing);
			} else if(currentNode instanceof Operand) {
				if(currentNode instanceof Identifier) {
					currentNode.setType(enclosing);
					SymbolType ss = currentNode.getSymType();
					//System.out.println("Identifier " + currentNode.toPrint() + " type " + ss.getTypeName());
				}
				//System.out.println(currentNode.getSymType());
				//enclosing.resolve(currentNode.getSymType().getTypeName());
			} else if(currentNode instanceof BitWiseOp) {
				lhs = children.get(index - 1);
				rhs = children.get(index + 1);

				validate(lhs, currentNode, rhs, enclosing);
			} else if(currentNode instanceof Expression) {
				currentNode.decorateFirst(enclosing);
			}
		}


	}

	public SymbolType validate(Subtree lhs, Subtree operator, Subtree rhs, Scope enclosing) throws UndefinedTypeException {
		SymbolType result = null;				//result type to be returned


		//first thing to do is check that the lhs and rhs are valid for the expression
		//can either by a charliteral, intliteral, floatliteral, identifier, or function-call

		//resolves the types for the lhs and rhs of the expression. resulting symbol type's
		//left and right should be int32 or float64 to be valid for mathematical operations

		SymbolType left = (BuiltInTypeSymbol)enclosing.resolve(lhs.getSymType().getTypeName());
		SymbolType right = (BuiltInTypeSymbol)enclosing.resolve(rhs.getSymType().getTypeName());

		//checks that both the left and right side of the expressions have types
		//that have been defined in this scope
		if(left == null) {
			throw new UndefinedTypeException(left.getTypeName());
		} else if(right == null) {
			throw new UndefinedTypeException(right.getTypeName());
		}

		//by this point we know that both lhs and rhs are valid BuiltInTypes.
		//two cases, can be bitwise operator or math operation, these two
		//cases will be handled differently as far as the type of operands that
		//they allow.
		MathOp temp = (MathOp)operator;
		if(operator instanceof MathOp) {
			if(temp.validOp(left) && temp.validOp(right)) {
				System.out.println("Both operations were valid");

				if(right.getTypeName().equals("float64") ||
					left.getTypeName().equals("float64")) {
					result = (BuiltInTypeSymbol)enclosing.resolve("float64");
				} else {
					result = (BuiltInTypeSymbol)enclosing.resolve("int32"); 
				}

			} else {
				throw new IllegalOperationException(operator.getClass().getName(), "");
			}

		} else if(operator instanceof BitWiseOp) {

		}


		//need to sort by binary operation etc.

		//need to sort by whether the types are valid when combined with the math
		//operation, if they are of the same type then that will be the resulting type
		//otherwise casting is necessary.
		return result;

	}

	protected void readExpression() {
		if(isUnary())
			addChild(new UnaryExpression(token, it));
		else
			addOperandChild(token);
		if(continueReading())
			readRemainingExpr();
	}

	protected boolean isUnary() {
		boolean isUnary = false;
		switch(token.getTokenType()) {
			case "TILDE":
				isUnary = true;
				break;
			case "EXCLAMATION_POINT":
				isUnary = true;
				break;
			case "MINUS":
				isUnary = true;
				break;
			default:
				break;
		}
		return isUnary;
	}

	protected void addOperandChild(Token current) {
		switch(current.getTokenType()) {
			case "IntIdentifier":
				addChild(new IntLiteral(current, it)); 
				match("IntIdentifier");
				break;
			case "FloatIdentifier":
				addChild(new FloatLiteral(current, it));
				match("FloatIdentifier");
				break;
			case "StringIdentifier":
				addChild(new Identifier(current, it));
				match("StringIdentifier");
				break;
			case "OPEN_PARENTHESIS":
				match("OPEN_PARENTHESIS");
				Expression sub = new Expression(token, it);
				addChild(sub);
				match("CLOSE_PARENTHESIS");
				break;
			case "String_Literal":
				addChild(new StringLit(current, it));
				match("String_Literal");
				break;
			case "Char_Literal":
				addChild(new CharLit(current, it));
				match("Char_Literal");
				break;
			default:
				break;
		}
	}

	protected int addOperatorChild(Token current) {
		int precedence = 0;
		switch(current.getTokenType()) {
			case "PLUS":
				precedence = 10; 
				addChild(new Addition(current, it));
				match("PLUS");
				break;
			case "MINUS":
				precedence = 10;
				match("MINUS");
				addChild(new Subtraction(current, it));
				break;
			case "ASTERISK":
				precedence = 11;
				addChild(new Multiplication(current, it));
				match("ASTERISK");
				break;
			case "BACKSLASH":
				precedence = 11;
				addChild(new Division(current, it));
				match("BACKSLASH");
				break;
			case "ASSIGNMENT_OPERATOR":
				precedence = 1;
				addChild(new Assignment(current, it));
				match("ASSIGNMENT_OPERATOR");
				break;
			case "RELATIONAL_GREATER_THAN":
				precedence = 8;
				addChild(new GreaterThan(current, it));
				match("RELATIONAL_GREATER_THAN");
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				precedence = 8;
				addChild(new GreaterThanEqual(current, it));
				match("RELATIONAL_GREATER_EQUALTO");
				break;
			case "RELATIONAL_LESS_THAN":
				precedence = 8;
				addChild(new LessThan(current, it));
				match("RELATIONAL_LESS_THAN");
				break;
			case "RELATIONAL_LESS_EQUALTO":
				precedence = 8;
				addChild(new LessThanEqual(current, it));
				match("RELATIONAL_LESS_EQUALTO");
				break;
			case "BITWISE_AND":
				precedence = 6;
				addChild(new BitwiseAnd(current, it));
				match("BITWISE_AND");
				break;
			case "LOGICAL_AND":
				precedence = 3;
				addChild(new LogicalAnd(current, it));
				match("LOGICAL_AND");
				break;
			case "BITWISE_OR":
				precedence = 4;
				addChild(new BitwiseOr(current, it));
				match("BITWISE_OR");
				break;
			case "LOGICAL_OR":
				precedence = 2;
				addChild(new LogicalOr(current, it));
				match("LOGICAL_OR");
				break;
			case "BITWISE_XOR":
				precedence = 5;
				addChild(new XoR(current, it));
				match("BITWISE_XOR");
				break;
			case "LOGICAL_NOT":
				precedence = 7;
				addChild(new Inequality(current, it));
				match("LOGICAL_NOT");
				break;
			case "OUTPUT":
				precedence = 9;
				addChild(new LeftShift(current, it));
				match("OUTPUT");
				break;
			case "INPUT":
				precedence = 9;
				addChild(new RightShift(current, it));
				match("INPUT");
				break;
			case "EQUALITY":
				precedence = 7;
				addChild(new Equality(current, it));
				match("EQUALITY");
				break;
			default:
				break;
		}
		return precedence;
	}

	private void addOperandChild(Token current, boolean match) {
		switch(current.getTokenType()) {
			case "IntIdentifier":
				addChild(new IntLiteral((Integer)current.getVal())); 
				break;
			case "FloatIdentifier":
				addChild(new FloatLiteral(current, it));
				break;
			case "StringIdentifier":
				addChild(new Identifier(current, it));
				break;
			case "OPEN_PARENTHESIS":
				match("OPEN_PARENTHESIS");
				addChild(new Expression(current, it));
				break;
			case "String_Literal":
				addChild(new StringLit(current, it));
				break;
			case "Char_Literal":
				addChild(new CharLit(current, it));
				break;
			default:
				break;
		}
	}

	private int addOperatorChild(Token current, boolean match) {
		int precedence = 0;
		switch(current.getTokenType()) {
			case "PLUS":
				precedence = 10; 
				addChild(new Addition(current, it));
				break;
			case "MINUS":
				precedence = 10;
				addChild(new Subtraction(current, it));
				break;
			case "ASTERISK":
				precedence = 11;
				addChild(new Multiplication(current, it));
				break;
			case "BACKSLASH":
				precedence = 11;
				addChild(new Division(current, it));
				break;
			case "ASSIGNMENT_OPERATOR":
				precedence = 1;
				addChild(new Assignment(current, it));
				break;
			case "RELATIONAL_GREATER_THAN":
				precedence = 8;
				addChild(new GreaterThan(current, it));
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				precedence = 8;
				addChild(new GreaterThanEqual(current, it));
				break;
			case "RELATIONAL_LESS_THAN":
				precedence = 8;
				addChild(new LessThan(current, it));
				break;
			case "RELATIONAL_LESS_EQUALTO":
				precedence = 8;
				addChild(new LessThanEqual(current, it));
				break;
			case "BITWISE_AND":
				precedence = 6;
				addChild(new BitwiseAnd(current, it));
				break;
			case "LOGICAL_AND":
				precedence = 3;
				addChild(new LogicalAnd(current, it));
				break;
			case "BITWISE_OR":
				precedence = 4;
				addChild(new BitwiseOr(current, it));
				break;
			case "LOGICAL_OR":
				precedence = 2;
				addChild(new LogicalOr(current, it));
				break;
			case "BITWISE_XOR":
				precedence = 5;
				addChild(new XoR(current, it));
				break;
			case "LOGICAL_NOT":
				precedence = 7;
				addChild(new Inequality(current, it));
				break;
			case "OUTPUT":
				precedence = 9;
				addChild(new LeftShift(current, it));
				break;
			case "INPUT":
				precedence = 9;
				addChild(new RightShift(current, it));
				break;
			case "EQUALITY":
				precedence = 7;
				addChild(new Equality(current, it));
				break;
			default:
				break;
		}
		return precedence;
	}

	protected boolean continueReading() {
		boolean continueReading = true;
		switch(token.getTokenType()) {
			case "COMMA":
				continueReading = false;
				break;
			case "CLOSE_PARENTHESIS":
				continueReading = false;
				break;
			case "SEMICOLON":
				continueReading = false;
				break;
			case "CLOSE_BRACKET":
				continueReading = false;
				break;
			default:
				break;
		}
		return continueReading;
	}

	protected void readRemainingExpr() {
		int currPrec = 0;
		int nextPrec = 20;

		currPrec = addOperatorChild(token);

		if(isUnary()){
			addChild(new UnaryExpression(token, it));
		}
		else{
			addOperandChild(token);
		}
		while(continueReading()) {
			nextPrec = addOperatorChild(token);
		 	if(nextPrec > currPrec) {
		 		Subtree operator = new Subtree(children.get(children.size() - 1));
		 		Subtree leftHand = new Subtree(children.get(children.size() - 2));
		 		Expression toAdd = new Expression(leftHand, operator, token, it);
		 		children.remove(children.get(children.size() - 1));
		 		children.remove(children.get(children.size() - 1));
		 		addChild(toAdd);
	 	
		 	} else {
		 		if(isUnary())
		 			addChild(new UnaryExpression(token, it));
		 		else
		 			addOperandChild(token);
		 	}
		}
	}

	@Override
	public void print(){
		printUp("+---");

		System.out.println(print + "(,) " 
			+ " expression");

		printUp("+---");

		String toPrint = print + this.toPrint();
		System.out.println(toPrint);
	}

	@Override
	public String toPrint() {
		String retVal = "";

		for(int index = 0; index < children.size(); index++) {
			retVal += children.get(index).toPrint();
		}
		return retVal;
	}

}

class UnaryExpression extends Subtree {

	UnaryExpression(Token t, Iterator<Token> i){
		super(t, i);
		addUnaryChild();
		addOperandChild();
	}

	private void addUnaryChild() {
		switch(token.getTokenType()) {
			case "TILDE":
				addChild(new Tilde(token, it));
				match("TILDE");
				break;
			case "EXCLAMATION_POINT":
				addChild(new Not(token, it));
				match("EXCLAMATION_POINT");
				break;
			case "MINUS":
				addChild(new Negative(token, it));
				match("MINUS");
				break;
			default:
				break;
		}
	}

	private void addOperandChild() {
		switch(token.getTokenType()) {
			case "IntIdentifier":
				addChild(new IntLiteral(token, it));
				match("IntIdentifier");
				break;
			case "FloatIdentifier":
				addChild(new FloatLiteral(token, it));
				match("FloatIdentifier");
				break;
			case "StringIdentifier":
				addChild(new Identifier(token, it));
				match("StringIdentifier");
				break;
			case "OPEN_PARENTHESIS":
				match("OPEN_PARENTHESIS");
				addChild(new Expression(token, it));
				match("CLOSE_PARENTHESIS");
				break;
			case "String_Literal":
				addChild(new StringLit(token, it));
				match("String_Literal");
				break;
			case "Char_Literal":
				addChild(new CharLit(token, it));
				match("Char_Literal");
				break;
			default:
				break;
		}
	}
}

class Operand extends Subtree {
	Operand(Token t, Iterator<Token> i){
		super(t, i);
	}

	Operand(int current) {
		token = new IntIdentifier(current, 0, 0);
	}

	@Override
	public String toPrint() {
		return "";
	}
}

class Identifier extends Operand {
	Identifier(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public void setType(Scope enclosing) throws UndefinedTypeException {
		Symbol defined = enclosing.resolve((String)token.getVal());

		if(defined == null) {
			throw new UndefinedTypeException(toPrint());
		}

		type = defined.getType();
	}

	@Override
	public String toPrint() {
		return (String)token.getVal();
	}
}

//------------------------ StringLit should be done ---------------------------

class StringLit extends Operand {
	StringLit(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		return (String)token.getVal();
	}
}

//----------------------- IntLiteral should be done ---------------------------

class IntLiteral extends Operand {
	IntLiteral(Token t, Iterator<Token> i){
		super(t, i);
		type = new BuiltInTypeSymbol("int32");
	}

	//constructor was trying to fix a bug of sorts, fairly certain
	//the bug is no longer present but I am not positive
	IntLiteral(int current) {
		super(current);
		type = new BuiltInTypeSymbol("int32");
	}

	@Override
	public String toPrint() {
		return Integer.toString((Integer)token.getVal());
	}
}

//----------------------- FloatLiteral should be done -------------------------

class FloatLiteral extends Operand {
	FloatLiteral(Token t, Iterator<Token> i){
		super(t, i);
		type = new BuiltInTypeSymbol("float64");
	}

	@Override
	public String toPrint() {
		return Float.toString((Float)token.getVal());
	}
}

//------------------------- CharLit should be done ----------------------------

class CharLit extends Operand {
	CharLit(Token t, Iterator<Token> i){
		super(t, i);
		type = new BuiltInTypeSymbol("byte");
	}

	@Override
	public String toPrint() {
		return "'" + Character.toString((Character)token.getVal()) + "'";
	}
}

class FunctionCall extends Operand {
	FunctionCall(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class TypeCast extends Operand {
	TypeCast(Token t, Iterator<Token> i){
		super(t, i);
	}

	private void sort() {
		switch(token.getTokenType()) {
			case "KEYWORD_INT32":
				match("KEYWORD_INT32");
				addChild(new IntTypeCast(token, it));
				break;
			case "KEYWORD_BYTE":
				match("KEYWORD_BYTE");
				addChild(new ByteTypeCast(token, it));
				break;
			case "KEYWORD_FLOAT":
				match("KEYWORD_FLOAT");
				addChild(new FloatTypeCast(token, it));
				break;
			default:
				break;
		}
	}
}

class FloatTypeCast extends TypeCast {
	FloatTypeCast(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class ByteTypeCast extends TypeCast {
	ByteTypeCast(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class IntTypeCast extends TypeCast {
	IntTypeCast(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class Negative extends Subtree {
	Negative(Token t, Iterator<Token> i){
		super(t, i);
	}
}

class MathOp extends Subtree {
	MathOp(Token t, Iterator<Token> i){
		super(t, i);
	}

	public void decorateExpr(Scope enclosing) throws UndefinedTypeException, IllegalOperationException {}

	public boolean validOp(SymbolType operand) { return false; }

	@Override
	public String toPrint() {
		return token.getTokenType();
	}

}

class Addition extends MathOp {
	Addition(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		return "+";
	}

	@Override
	public boolean validOp(SymbolType operand) {
		boolean isValid = false;

		if(operand.getTypeName().equals("int32")) {
			isValid = true;
		} else if(operand.getTypeName().equals("float64")) {
			isValid = true;
		}

		return isValid;
	}

	/*	First thing to check for an expression is that the operand is valid
	 *	for the expression type. Mathematical operations can only be performed
	 *	on int32 and float64 types.
	 */

	// @Override
	// public void decorateExpr(Scope enclosing) throws UndefinedTypeException, IllegalOperationException {
	// 	// throw new IllegalOperationException();
	// 	// throw new RuntimeException();
	// 	//System.out.println("decorateExpr entered in Addition");
	// 	//System.out.println(token.getClass());
	// 	if(token instanceof IntIdentifier) {
		
	// 	} else if(token instanceof FloatIdentifier) {

	// 	} else if(token instanceof StringIdentifier) {
	// 		Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
	// 		//System.out.println("previouslyDefined is " + previouslyDefined);
	// 		if(previouslyDefined == null) {
	// 			throw new UndefinedTypeException(token.getName());
	// 		}
	// 		type = previouslyDefined.getType();
	// 	} else {
	// 		System.out.println(token.getClass());
	// 		throw new IllegalOperationException("+", token.getTokenType());
	// 	}

	// }
}

class Subtraction extends MathOp {
	Token unaryOperator = null;

	Subtraction(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "-" + token.getVal(); 
		return retVal;
	}

	public void decorateExpr(Scope enclosing) throws IllegalOperationException, UndefinedTypeException {
		if(token instanceof IntIdentifier) {
		
		} else if(token instanceof FloatIdentifier) {

		} else if(token instanceof StringIdentifier) {
			Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
			if(previouslyDefined == null) {
				throw new UndefinedTypeException(token.getName());
			}
			type = previouslyDefined.getType();
		} else {
			throw new IllegalOperationException("-", token.getTokenType());
		}

	}
}

class Multiplication extends MathOp {
	Token unaryOperator = null;

	Multiplication(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "*"; 
		return retVal;
	}

	public void decorateExpr(Scope enclosing) throws IllegalOperationException, UndefinedTypeException {
		if(token instanceof IntIdentifier) {
		
		} else if(token instanceof FloatIdentifier) {

		} else if(token instanceof StringIdentifier) {
			Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
			if(previouslyDefined == null) {
				throw new UndefinedTypeException(token.getName());
			}
			type = previouslyDefined.getType();
		} else {
			throw new IllegalOperationException("*", token.getTokenType());
		}

	}
}

class Division extends MathOp {
	Token unaryOperator = null;

	Division(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "/ " + token.getVal(); 
		return retVal;
	}

	public void decorateExpr(Scope enclosing) throws IllegalOperationException, UndefinedTypeException {
		if(token instanceof IntIdentifier) {
		
		} else if(token instanceof FloatIdentifier) {

		} else if(token instanceof StringIdentifier) {
			Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
			if(previouslyDefined == null) {
				throw new UndefinedTypeException(token.getName());
			}
			type = previouslyDefined.getType();
		} else {
			throw new IllegalOperationException("/", token.getTokenType());
		}
	}
}

class Tilde extends Subtree {		//bitwise not
	Token unaryOperator = null;

	Tilde(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "~ " + token.getVal(); 
		return retVal;
	}
}

class Assignment extends MathOp {
	Token unaryOperator = null;

	Assignment(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "= " + token.getVal(); 
		return retVal;
	}
}

/*	Class: GreaterThan	
 *	Operation Type: boolean
 *	
 *	Semantic Rules:
 *	1.	A compile-time error shall be issued if the operand to a boolean
 *		operator is of floating-point type.
 *		(aka only int32 types are accepted)
 */

class GreaterThan extends Subtree {
	Token unaryOperator = null;

	GreaterThan(Token t, Iterator<Token> i){
		super(t, i);
	}
	public void decorateExpr(Scope enclosing) throws UndefinedTypeException, IllegalOperationException {
		if(token instanceof IntIdentifier) {
		
		} else if(token instanceof StringIdentifier) {
			Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
			if(previouslyDefined == null) {
				throw new UndefinedTypeException(token.getName());
			} else if(previouslyDefined != null)
			type = previouslyDefined.getType();
		} else {
			throw new IllegalOperationException("+", token.getTokenType());
		}

	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "> " + token.getVal(); 
		return retVal;
	}
}

class GreaterThanEqual extends Subtree {
	Token unaryOperator = null;

	GreaterThanEqual(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += ">= " + token.getVal(); 
		return retVal;
	}
}

class LessThan extends Subtree {
	Token unaryOperator = null;

	LessThan(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "< " + token.getVal(); 
		return retVal;
	}
}

class LessThanEqual extends Subtree {
	Token unaryOperator = null;

	LessThanEqual(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "<= " + token.getVal(); 
		return retVal;
	}
}

class BitwiseAnd extends Subtree {
	Token unaryOperator = null;

	BitwiseAnd(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "& " + token.getVal(); 
		return retVal;
	}
}

class LogicalAnd extends Subtree {
	Token unaryOperator = null;

	LogicalAnd(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "&& " + token.getVal(); 
		return retVal;
	}
}

class BitwiseOr extends MathOp {
	Token unaryOperator = null;

	BitwiseOr(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "| " + token.getVal(); 
		return retVal;
	}
}

class LogicalOr extends MathOp {
	Token unaryOperator = null;

	LogicalOr(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "|| " + token.getVal(); 
		return retVal;
	}
}

class Not extends MathOp {
	Token unaryOperator = null;

	Not(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "! " + token.getVal(); 
		return retVal;
	}
}

class XoR extends MathOp {
	Token unaryOperator = null;

	XoR(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "^ " + token.getVal(); 
		return retVal;
	}
}

class Inequality extends MathOp {
	Token unaryOperator = null;

	Inequality(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "!= " + token.getVal(); 
		return retVal;
	}
}

class LeftShift extends MathOp {
	Token unaryOperator = null;

	LeftShift(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "<< " + token.getVal(); 
		return retVal;
	}
}

class RightShift extends MathOp {
	Token unaryOperator = null;

	RightShift(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += ">> " + token.getVal(); 
		return retVal;
	}
}

class Equality extends MathOp {
	Token unaryOperator = null;

	Equality(Token t, Iterator<Token> i){
		super(t, i);
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "== " + token.getVal(); 
		return retVal;
	}
}

class BitWiseOp extends Subtree {

}