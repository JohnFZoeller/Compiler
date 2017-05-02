import java.io.*;
import java.util.*;

class SyntaxParser {
	private Token currentTok;
	private Iterator<Token> i;
	private boolean firstOne;
	private String indent;
	private int row, col;

	public SyntaxParser(Lex lex){
		currentTok = null;
		i = lex.iterator();
		firstOne = true;
		indent = "";
		row = col = 0;
	}

	/*
		Main match function for the program. Logic is based off of the fact that the only
		time this match function will be called is in the case of a new statement in the program.
		Function matches currentTok's token type
	*/

	public void match(){
		switch(currentTok.getTokenType()) {
			case "for":		forr();
							break;
			case "while":	whilee();
							break;
			case "if":		iff();
							break;
			case "print":	print();
							break;
			case "return":	retur();
							break;
			case "type":	type();
							break;
			case "exit":	exit();
							break;
			case "function":func();
							break;
			case "var":		var();
							break;
			case "static":	var();
							break;
			case "const":	var();
							break;
			default:		System.exit(0);
							break;
		}
	}

	/******************MAIN FUNCTIONAILITY*********************/
	
	public void parse(){
		System.out.println("(" + row + ", " + col + ")" + " " + 
			System.identityHashCode(this) + " list");

		indentUp();

		while(i.hasNext()){
			if(firstOne){
				readNextTok();					//reads next Token into currentTok
				firstOne = false;
			}

			match();
			resetIndent();	
		}						//matches currentTok to a statement type, toInsert is
	}

	public void match(String expect){
		if(currentTok.getTokenType().equals(expect)){
			readNextTok();
		}
		else throw new 
			Error("Expected: " + expect + "Got: " + currentTok.getTokenType());
	}

	public void readNextTok() {
		if(i.hasNext()){
			currentTok = i.next();

			if(currentTok != null){

				System.err.println(indent + "(" + row + ", " 
					+ runningCol() + ")" + " " 
					+ System.identityHashCode(currentTok)
					+ " " + currentTok.getTokenType() + " " 
					+ currentTok.getName());
			}
		}
		else 
			System.exit(0);
	}

	/******************STATEMENT TYPES*************************/

	public void forr(){
		match("for");

		match("OPEN_PARENTHESIS");
		expression();
		match("SEMICOLON");
		expression();
		match("SEMICOLON");
		expression();
		match("CLOSE_PARENTHESIS");

		block();
	}

	public void whilee(){
		match("while");

		match("OPEN_PARENTHESIS");
		expression();
		match("CLOSE_PARENTHESIS");

		block();
	}

	public void iff(){
		match("if");

		match("OPEN_PARENTHESIS");
		expression();
		match("CLOSE_PARENTHESIS");

		block();

		if(isElse()){
			match("else");
			block();
		}
	}

	public void print(){
		match("print");

		expression();

		match("SEMICOLON");
	}		

	public void retur(){
		match("return");

		if(isExpresh())
			expression();

		match("SEMICOLON");
	}	

	public void type(){
		match("type");
		match("StringIdentifier");

		typeDescriptor();

		match("SEMICOLON");
	}

	public void exit(){
		match("exit");								//match exit

		if(isExpresh())								//optional expression next?
			expression();								//parse it

		match("SEMICOLON");
	}

	public void func(){
		System.out.println(indent + "+---name");
		match("function");
		match("StringIdentifier");
		match("OPEN_PARENTHESIS");

		if(hasParams())
			params();

		match("CLOSE_PARENTHESIS");

		if(hasTypeDesc())
			typeDescriptor();

		block();
	}

	public void var(){
		if(currentTok.getTokenType().equals("static"))
			match("static");
		
		if(currentTok.getTokenType().equals("const"))
			match("const");

		match("var");
		match("StringIdentifier");

		//assignment case ELSE declaration case
		if(currentTok.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			expression();
		} 
		else 
			typeDescriptor();

		match("SEMICOLON");
	}


	/******************SUPPORT STATEMENTS*********************/

	public void typeDescriptor(){
		naTypeDescriptor();

		if(isDimensh())								//optional expression next?
			dimensh();								//parse it
	}

	public void naTypeDescriptor(){

		// record-descriptor ELSE IF identifier ELSE basic-type
		if(currentTok.getTokenType().equals("record")){
			match("record");
			fieldDeclarations();
			match("end");
		}
		else if(currentTok.getTokenType().equals("StringIdentifier")){
			match("StringIdentifier");
		}
		else{
			switch(currentTok.getTokenType()){
				case "byte" : 	match("byte"); break;
				case "int32": 	match("int32"); break;
				case "float64": match("float64"); break;
				default : break;
			}
		}
	}

	public void block(){
		match("OPEN_BRACE");
		
		while(!currentTok.getTokenType().equals("CLOSE_BRACE"))
			match();

		match("CLOSE_BRACE");
	}

	public void parameter(){
		if(currentTok.getTokenType().equals("ref"))
			match("ref");

		if(currentTok.getTokenType().equals("const"))
			match("const");

		match("StringIdentifier");

		if(currentTok.getTokenType().equals("ASSIGNMENT_OPERATOR")){
			match("ASSIGNMENT_OPERATOR");
			expression();
		}
		else{
			naTypeDescriptor();

			if(hasWilds()){
				match("OPEN_BRACKET");
				dimWilds();
				match("CLOSE_BRACKET");
			}
		}
	}

	public void params(){
		//ORIGINAL LEFT RECURSION : parameters ::= ( parameters , )* parameter
		//THIS IS SIMPIFIED FORM, IN NON-SIMPLIFIED FORM THE LEFT RECURSION IS
		//PARAMETERS -> PARAMETERS , PARAM || PARAM

		//TRANSLATED TO RIGHT RECURSION

		//PARAMETERS -> PARAM Z
		//         Z -> , PARAM Z || empty

		//example string (int x, int y, char z)
		//match(param); z(); 
		//public void z(){ if(currentTok != ','){ return; } match(comma); match(param); z(); }
		parameter();
		z();
	}

	public void z(){
		if(!currentTok.getTokenType().equals("COMMA")) return;

		match("COMMA");
		parameter();
		z();
	}

	public void dimWilds(){
		wildCard();
		x();
	}

	public void wildCard(){
		//collapse
		match("ASTERISK");
	}

	public void x(){
		if(!currentTok.getTokenType().equals("COMMA")) return;

		match("COMMA");
		wildCard();
		x();
	}

	public void fieldDeclaration(){
		match("StringIdentifier");
		typeDescriptor();
	}

	public void fieldDeclarations(){
		fieldDeclaration();
		y();
	}

	public void y(){
		if(currentTok.getTokenType().equals("end")) return;

		match("COMMA");
		fieldDeclaration();
		y();
	}

	public void dimensh(){
		match("OPEN_BRACKET");
		expressions();
		match("CLOSE_BRACKET");
	}
	/*********************TESTERS**************************/

	public boolean hasTypeDesc(){
		//collapse
		return !currentTok.getTokenType().equals("OPEN_BRACE");
	}

	public boolean hasParams(){
		//collapse
		return !currentTok.getTokenType().equals("CLOSE_PARENTHESIS");
	}

	public boolean isDimensh(){
		//collapse
		return currentTok.getTokenType().equals("OPEN_BRACKET");
	}

	public boolean isElse(){
		//collapse
		return currentTok.getTokenType().equals("else");
	}

	public boolean hasWilds(){
		//collapse
		return currentTok.getTokenType().equals("OPEN_BRACKET");
	}

	/***********************PLACE HOLDERS**********************/

	public boolean isExpresh(){ return true; }

	/**********************EXPRESSIONS*************************/

public void expressions() {
	switch(currentTok.getTokenType()) {
		case "OPEN_PARENTHESIS":
			exprRest();
			break;
		case "EXCLAMATION_POINT":
			exprRest();
			break;
		case "MINUS":
			exprRest();
			break;
		case "TILDE":
			exprRest();
			break;
		case "KEYWORD_INT32":
			match("KEYWORD_INT32");
			typeCast();
			break;
		case "KEYWORD_FLOAT64":
			match("KEYWORD_FLOAT64");
			typeCast();
			break;
		case "KEYWORD_BYTE":
			match("KEYWORD_BYTE");
			typeCast();
			break;
		case "IDENTIFIER":
			match("IDENTIFIER");
			varOrFunc();
			break;
		case "STRING_IDENTIFIER":
			match("STRING_IDENTIFIER");
			exprRest();
			break;
		case "INT_IDENTIFIER":
			match("INT_IDENTIFIER");
			exprRest();
			break;
		case "FLOAT_IDENTIFIER":
			match("FLOAT_IDENTIFIER");
			exprRest();
			break;
		default:
			break; 
	}
}

//Not sure about the logic of this one
public void varOrFunc() {
	if(currentTok.getTokenType().equals("SUBSCRIPT")) {
		variable();
	} else if(currentTok.getTokenType().equals("LEFT_PARENTHESIS")) {
		match("LEFT_PARENTHESIS");
		if(currentTok.getTokenType().equals("PERIOD")) {
			variable();
		} else {
			funcCall();
		}
	}
}

public void typeCast() {
	match("LEFT_PARENTHESIS");
	expression();
	match("RIGHT_PARANTHESIS");
}

public void expression() {
	switch(token.getTokenType()) {
		case "OPEN_PARENTHESIS":					//sub-expression
			match("OPEN_PARENTHESIS");
			expression();
			break;
		case "INT_IDENTIFIER":	//integer, ready for operator
			match("INT_IDENTIFIER")
			exprRest();
			break;
		case "FLOAT_IDENTIFIER":	//float, ready for operator
			readNextToken();
			exprRest();
			break;
		case "BYTE_IDENTIFIER":	//byte, math expressions not allowed
			readNextToken();
			byteExpr();
			break;
		//special case, could be a function call or variable
		/*case ("IDENTIFIER"):
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
		case ("STRING_IDENTIFIER"):	//lhs is string
			string();
			break;
			*/
		default:
			exprRest();
			break;
	}

}
//NEEDS TO HAVE SOMETHING IN THE CASE THAT A SUB-EXPRESSION IS ENCOUNTERED
public boolean matchOperand() {
	isMatch = false;
	if(token.getTokenType().equals("INT_IDENTIFIER")) {
		match("INT_IDENTIFIER");
	} else if (token.getTokenType().equals("FLOAT_IDENTIFIER")) {
		match("FLOAT_IDENTIFIER");
	} else if(token.getTokenType().equals("IDENTIFIER")) {
		match("IDENTIFIER");
	} else if (token.getTokenType().equals("STRING_IDENTIFIER")) {
			match("STRING_IDENTIFIER");
	} else if(token.getTokenType().equals("OPEN_PARENTHESIS")){
		match("OPEN_PARENTHESIS");
		expression();
	} else {
		//error();
	}
}

public void exprRest() {
	//NEED SOME SORT OF STOPPING POINT OR BASE CASE

	//match to a math expression
	//read in the next part of the expression
	switch(token.getTokenType()) {
		case "COMMA":
			match("COMMA");
			expression();
			break;
		case "SEMICOLON":
			match("SEMICOLON");
			break;
		case "CLOSE_PARENTHESIS":
			match("CLOSE_PARENTHESIS");
			break;
		case "EXCLAMATION_POINT":
			match("EXCLAMATION_POINT")
			matchOperand();
			exprRest();
			break;
		case "TILDE":
			match("TILDE")
			matchOperand();
			exprRest();
			break;
		case "MINUS":
			match("MINUS")
			matchOperand();
			exprRest();
			break;
		case "CLOSE_BRACKET":
			match("CLOSE_BRACKET");
			break;
		case "OPEN_BRACKET":
			match("OPEN_BRACKET");
			expression();
			break;
		case "ASSIGNMENT_OPERATOR":
			match("ASSIGNMENT_OPERATOR");
			expression();
			break;
		case "PLUS":
			match("PLUS");
			matchOperand();
			exprRest();
			break;
		case "MINUS":
			match("MINUS");
			matchOperand();
			exprRest();
			break;
		case "ASTERISK":
			match("ASTERISK");
			matchOperand();
			exprRest();
			break;
		case "BACKSLASH":
			match("BACKSLASH");
			matchOperand();
			exprRest();
			break;
		case "BITWISE_OR":
			match("BITWISE_OR");
			//something special here
			break;
		case "BITWISE_AND":
			match("BITWISE_AND");
			//something special here
			break;
		case "LOGICAL_OR":
			match("LOGICAL_OR");
			//something special here
			break;
		case "LOGICAL_AND":
			match("LOGICAL_AND");
			//something special here
			break;
		default:
			break;
	}
}

	/***********************UTILITIES************************/

	public void indentUp(){
		//collapse
		indent += "+---";
	}

	public void resetIndent(){
		//collapse
		indent = "+---";
	}

	public int runningCol(){
		col = (currentTok.getCol() < col) ? 
		(col + currentTok.getCol()) : currentTok.getCol();

		return col;
	}
}