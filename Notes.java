private Token currentTok;
private Token lookAhead;
private BufferedReader tokenReader;


private static statementNode() {
	if(currentTok instanceof Keyword) {
		switch(currentTok.getTokenType()) {
			case "for":		forStatement();
			case "while":	whileStatement();
			case "if":		ifStatement();
			case "print":	printStatement();
			case "return":	returnStatement();
			case "exit":	exitStatement();
			case "type":	declStatement();
			case "func":	declStatement();
			case "var":		declStatement();
			//NEED TO ACCOUNT FOR EXPRESSION
			default:
				break;
		}
	} else if(currentTok.getTokenType() == '{') {
		return new BlockStatement(tokenReader);
	}
}

private static whileStatement() {
	currentTok = (Token)tokenReader.read();
	ASTNode toInsert;

	if(currentTok == '(') {
		toInsert = new ForNode();
		ForNode.parseStatement(tokenReader);
	} else {
		//	This is definitely not the right syntax
		//	Should also create our own Exception classes
		throw new ParseError();
	}
}


private static forStatement() {
	currentTok = (Token)tokenReader.read();
	ASTNode toInsert;
	bool success = false;

	if(currentTok == '(') {						//checks for valid syntax
		toInsert = new ForNode();				//creates new ForNode since we know that we at least have "for("
		ForNode.parser(tokenReader);			//function call, will call parser on each of it's data members
												//if all parser functions return true, then we know that toInsert is a valid Node

		/*
			class ForNode{
				
				* There are 3 cases for the assignExp (first expression in the for loop):
					1. int index = 0;
						for(index; ...)				name of variable
					2. int index;
						for(index = 0; ...)			name of variable
					3. -------
						for(int index = 0; ...)		variable identifier

				  We need to match which of these three cases we are dealing with. We will know by reading in a token
				  and checking whether it is a variable type such as int32, float64... etc.

				  NOTE: I think that these cases we actually don't deal with in the parser. I think the only thing we check is that it is an expression.

				private Expr assignExp;							//if currentTok == ';' we know to move on to condition
				private Expr condition;							//if currentTok == ';' we know to move on to increment
				private Expr increment;							//if currentTok == ')', we know the for statement is complete
				
				public bool parser(BufferedReader input) {
					bool successful = false;
					if(assignExpr.parser(input) == true) {
						if(condition.parser(input) == true) {
							if(increment.parser(input) == true && currentTok == ')') {
								successful == true;
							}
						}
					}
					return successful;
				}
			}

			class Expr {
				private Token value;		//ex: int32 token with the value '3'
				private ExprRest rest;		//the rest of the expression
				
				public bool parser(BufferedReader input) {		//we know that 
					bool successful = false;

				}
			}

			class ExprRest {
				private Operator operation;	//ex '+'
				private Token value;		//ex: int 32 token with the value of '6'
				private ExprRest rest;		//the rest of the expression
			}


		*/

	} else {
		//	This is definitely not the right syntax
		//	Should also create our own Exception classes
		throw new ParseError();
	}
}

private static ifStatement() {
	currentTok = (Token)tokenReader.read();
	ASTNode toInsert;

	if(currentTok == '(') {
		toInsert = new ForNode();
		ForNode.parseStatement(tokenReader);
	} else {
		//	This is definitely not the right syntax
		//	Should also create our own Exception classes
		throw new ParseError();
	}
}