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
		System.out.println("ASSEMBLY CODE");

		for(int i = 0; i < children.size(); i++){
			children.get(i).emit(consts);
		}

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

	public void setType(Scope e){;}  //temporary workaround

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

		//eventually gonna add these in everywhere
		if(token.getTokenType().equals("DOT")){
			match("DOT");
			match("StringIdentifier");
		}

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
			Expression child = new Expression();
			child.populate(it, token);
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

	@Override
	public void decorateFirst(Scope enclosing) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		symbol = new ExpressionSymbol(type);
		
		Subtree currentNode = null;
		if(children != null){
			for(int index = 0; index < children.size(); index++) {
				currentNode = children.get(index);

				//System.out.println(currentNode.getClass());
				if(currentNode instanceof ExprRest) {
					System.out.println("decorateFirst() entered in Expression");
					ExprRest tt = (ExprRest) children.get(index);
					tt.decorateExpr(enclosing);
				} else if(currentNode instanceof Expression) {
					currentNode.decorateFirst(enclosing);
				}
			}
		}
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
				Expression subexpression = new Expression(token, it,currPrec);
				addChild(subexpression);
			} else {
				ExprRest toAdd = new ExprRest(token, it);
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

	//had to add the closebracket case to make arrays work.
	//technically i think its incorrect tho... bc for something like this
	//var array int32[4];
	//array[2] = 4;
	//var john = array[2] + 6;
	//it would parse incorrectly
	//then again... i dont know expressions so well, so maybe it does work 
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
			case "CLOSE_BRACKET":
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
				toPrint += "" + children.get(i).toPrint() + "";
			}
		}
	}

	private void matchOperand() {
		switch(token.getTokenType()) {
			case "IntIdentifier":
				tokenType = Integer.toString((int)token.getVal());
				tokenDescrip = "Integer";
				match("IntIdentifier");
				break;
			case "FloatIdentifier":
				tokenType = Float.toString((float)token.getVal());
				tokenDescrip = "Float";
				match("FloatIdentifier");
				break;
			case "BYTE_IDENTIFIER":
				tokenType = Integer.toString((int)token.getVal());
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

	ExprRest() {}

	ExprRest(Token t, Iterator<Token> i){
		super(t, i);
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
		//Symbol temp = (BuiltInTypeSymbol)enclosing.resolve(getOpType());

		MathOp currentNode = null;
		System.out.println(children.size());
		if(children != null) {
			for(int index = 0; index < children.size(); index++) {
				System.out.println("decorateExpr entered in ExprRest");
				currentNode = (MathOp)children.get(index);
				currentNode.decorateExpr(enclosing);
			}
		//MathOp currentNode = null;
		if(children != null) {
			for(int index = 0; index < children.size(); index++) {
				//currentNode = (MathOp)children.get(index);
				//currentNode.decorateExpr(enclosing);
			}
		}

	}}

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

	private void populate() {
		matchOperator();
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
				match("PLUS");
				addChild(new Addition(token));
				match(token.getTokenType());
				break;
			case "MINUS":
				match("MINUS");
				addChild(new Subtraction(token));
				match(token.getTokenType());
				break;
			case "ASTERISK":
				match("ASTERISK");
				addChild(new Multiplication(token));
				match(token.getTokenType());
				break;
			case "BACKSLASH":
				match("BACKSLASH");
				addChild(new Division(token));
				match(token.getTokenType());
				break;
			case "TILDE":
				match("TILDE");
				addChild(new Tilde(token));
				match(token.getTokenType());
				break;
			case "ASSIGNMENT_OPERATOR":
				match("ASSIGNMENT_OPERATOR");
				addChild(new Assignment(token));
				match(token.getTokenType());
				break;
			case "RELATIONAL_GREATER_THAN":
				match("RELATIONAL_GREATER_THAN");
				addChild(new GreaterThan(token));
				match(token.getTokenType());
				break;
			case "RELATIONAL_GREATER_EQUALTO":
				match("RELATIONAL_GREATER_EQUALTO");
				addChild(new GreaterThanEqual(token));
				match(token.getTokenType());
				break;
			case "RELATIONAL_LESS_THAN":
				match("RELATIONAL_LESS_THAN");
				addChild(new LessThan(token));
				match(token.getTokenType());
				break;
			case "RELATIONAL_LESS_EQUALTO":
				match("RELATIONAL_LESS_EQUALTO");
				addChild(new LessThanEqual(token));
				match(token.getTokenType());
				break;
			case "BITWISE_AND":
				match("BITWISE_AND");
				addChild(new BitwiseAnd(token));
				match(token.getTokenType());
				break;
			case "LOGICAL_AND":
				match("LOGICAL_AND");
				addChild(new LogicalAnd(token));
				match(token.getTokenType());
				break;
			case "BITWISE_OR":
				match("BITWISE_OR");
				addChild(new BitwiseOr(token));
				match(token.getTokenType());
				break;
			case "LOGICAL_OR":
				match("LOGICAL_OR");
				addChild(new LogicalOr(token));
				match(token.getTokenType());
				break;
			case "EXCLAMATION_POINT":
				match("EXCLAMATION_POINT");
				addChild(new Not(token));
				match(token.getTokenType());
				break;
			case "BITWISE_XOR":
				match("BITWISE_XOR");
				addChild(new XoR(token));
				match(token.getTokenType());
				break;
			case "LOGICAL_NOT":
				match("LOGICAL_NOT");
				addChild(new Inequality(token));
				match(token.getTokenType());
				break;
			case "OUTPUT":
				match("OUTPUT");
				addChild(new LeftShift(token));
				match(token.getTokenType());
				break;
			case "INPUT":
				match("INPUT");
				addChild(new RightShift(token));
				match(token.getTokenType());
				break;
			case "EQUALITY":
				match("EQUALITY");
				addChild(new Equality(token));
				match(token.getTokenType());
				break;
			default:
				break;
		}
	}

	@Override
	public void print() {}

	@Override
	public String toPrint() {
		String retVal = ""; //+ opType + " " + uOperator + operandType + "";

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

class MathOp extends Subtree {

	public void decorateExpr(Scope enclosing) throws UndefinedTypeException, IllegalOperationException {}

}

class Addition extends MathOp {
	Addition(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "+" + token.getVal(); 
		return retVal;
	}

	/*	First thing to check for an expression is that the operand is valid
	 *	for the expression type. Mathematical operations can only be performed
	 *	on int32 and float64 types.
	 */

	@Override
	public void decorateExpr(Scope enclosing) throws UndefinedTypeException, IllegalOperationException {
		//throw new IllegalOperationException();
		//throw new RuntimeException();
		// System.out.println("decorateExpr entered in Addition");
		// //System.out.println(token.getClass());
		// if(token instanceof IntIdentifier) {
		
		// } else if(token instanceof FloatIdentifier) {

		// } else if(token instanceof StringIdentifier) {
		// 	Symbol previouslyDefined = (BuiltInTypeSymbol)enclosing.resolve(token.getName());
		// 	//System.out.println("previouslyDefined is " + previouslyDefined);
		// 	if(previouslyDefined == null) {
		// 		throw new UndefinedTypeException(token.getName());
		// 	}
		// 	type = previouslyDefined.getType();
		// } else {
		// 	System.out.println(token.getClass());
		// 	throw new IllegalOperationException("+", token.getTokenType());
		// }

	}
}

class Subtraction extends Subtree {
	Token unaryOperator = null;

	Subtraction(Token t){
		token = t;
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

class Multiplication extends Subtree {
	Token unaryOperator = null;

	Multiplication(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "*" + token.getVal(); 
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

class Division extends Subtree {
	Token unaryOperator = null;

	Division(Token t){
		token = t;
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

	Tilde(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "~ " + token.getVal(); 
		return retVal;
	}
}

class Assignment extends Subtree {
	Token unaryOperator = null;

	Assignment(Token t){
		token = t;
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

	GreaterThan(Token t){
		token = t;
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

	GreaterThanEqual(Token t){
		token = t;
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

	LessThan(Token t){
		token = t;
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

	LessThanEqual(Token t){
		token = t;
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

	BitwiseAnd(Token t){
		token = t;
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

	LogicalAnd(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "&& " + token.getVal(); 
		return retVal;
	}
}

class BitwiseOr extends Subtree {
	Token unaryOperator = null;

	BitwiseOr(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "| " + token.getVal(); 
		return retVal;
	}
}

class LogicalOr extends Subtree {
	Token unaryOperator = null;

	LogicalOr(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "|| " + token.getVal(); 
		return retVal;
	}
}

class Not extends Subtree {
	Token unaryOperator = null;

	Not(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "! " + token.getVal(); 
		return retVal;
	}
}

class XoR extends Subtree {
	Token unaryOperator = null;

	XoR(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "^ " + token.getVal(); 
		return retVal;
	}
}

class Inequality extends Subtree {
	Token unaryOperator = null;

	Inequality(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "!= " + token.getVal(); 
		return retVal;
	}
}

class LeftShift extends Subtree {
	Token unaryOperator = null;

	LeftShift(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "<< " + token.getVal(); 
		return retVal;
	}
}

class RightShift extends Subtree {
	Token unaryOperator = null;

	RightShift(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += ">> " + token.getVal(); 
		return retVal;
	}
}

class Equality extends Subtree {
	Token unaryOperator = null;

	Equality(Token t){
		token = t;
	}

	@Override
	public String toPrint() {
		String retVal = "";
		retVal += "== " + token.getVal(); 
		return retVal;
	}
}