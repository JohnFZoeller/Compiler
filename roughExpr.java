private Token token;
private int indent = 0;
/*
Expressions can either go to a '(' or 


*/

public void expressions() {
	//indicates that we are dealing with a list of expressions
	//each expression should be separated by a comma
	if(token.getTokenType().equals("(")) {		//if the currentToken equals "("
		expression();
	} else {
		expression();
	}
}

public void readNextToken() {
	if(input.ready()) {
		input.read();
	}
}

private void increaseIndent() {
	indent++;
}

/*
 *
 *
 *
*/

public void expression() {
	increaseIndent();
	switch(token.getTokenType()) {
		case ("("):					//sub-expression
			expression();
			break();
		case ("INT_IDENTIFIER"):	//integer, ready for operator
			numExpr();
			break;
		case ("FLOAT_IDENTIFIER"):	//float, ready for operator
			numExpr();
			break;
		case ("BYTE_IDENTIFIER"):	//byte, math expressions not allowed
			byteExpr();
			break;
		//special case, could be a function call or variable
		case ("IDENTIFIER"):
			funcCall();
			break;
		case ("KEYWORD_BYTE"):		//byte keyword, must be typecast
			typeCast();
			break;
		case ("KEYWORD_INT32"):		//int32 keyword, must be typecast
			typeCast();
			break;
		case ("KEYWORD_FLOAT64"):	//float64 keyword, must be typecast
			typeCast();
			break;
		case ("VAR_IDENTIFIER"):		//expression references declared variable
			varExpr();
			break;
		case ("["):					//must be subscript
			expressions();
			break;
		case ("STRING_IDENTIFIER"):	//lhs is string
			string();
			break;
		case (")"):
			return;
		case ("]"):
			return;
		default:
			exprRest();
			break();
	}
}

/*
 *	Called when an int32 type is on the lhs of the expression. If there is a float
 *	anywhere of the rhs then the the int32 type must be converted to float
 *	
 */

private void numExpr() {
	//reads the next token from the buffered reader
	readNextToken();
	//an operand is expected as the next token
	switch(token.getTokenType()) {
		case ("+"):
			addition();
			break;
		case ("-"):
			subtraction();
			break;
		case ("*"):
			multipication();
			break;
		case ("/"):
			division();
			break;
		case ("|"):
			or();
			break;
		case ("&"):
			and();
			break;
		case ("^"):
			exponent();
			break;
		case ("<<"):
			leftshift();
			break;
		case (">>"):
			rightshift();
			break;
		case ("=="):
			equality();
			break;
		case ("!="):
			inequality();
			break;
		case ("<"):
			less();
			break;
		case ("<="):
			lessEqual();
			break;
		case (">"):
			greater();
			break;
		case (">="):
			greaterEqual();
			break;
		case ("&&"):
			logicAnd();
			break;
		case ("||"):
			logicOr();
			break;
		case ("("):
			expression();
			break;
		default:
			break;
	}
}

private void typeCast() {
	readNextToken();
	if(token.getTokenType().equals("(")) {
		expression();
	}
}

private void varExpr() {
	readNextToken();
	switch(token.getTokenType()) {
		case ("["):
			expressions();
			break;
		case ("("):
			varExpr();
			break;
		default:
			break;
	}
}