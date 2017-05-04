import java.io.*;
import java.util.*;

public class SyntaxParser {
	private Token currentTok;
	private Iterator<Token> i;
	private boolean firstOne;
	private String indent;
	private int row, col;
	private Subtree root;

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
			case "for":		root.addChild(new For(currentTok, i));
							break;
			case "while":	root.addChild(new While(currentTok, i));
							break;
			case "if":		root.addChild(new If(currentTok, i));
							break;
			case "print":	root.addChild(new Print(currentTok, i));
							break;
			case "return":	root.addChild(new Retur(currentTok, i));
							break;
			case "type":	root.addChild(new Type(currentTok, i));
							break;
			case "exit":	root.addChild(new Exit(currentTok, i));
							break;
			case "function":root.addChild(new Func(currentTok, i));
							break;
			case "var":		root.addChild(new Var(currentTok, i));
							break;
			case "static":	root.addChild(new Var(currentTok, i));
							break;
			case "const":	root.addChild(new Var(currentTok, i));
							break;
			default:		System.out.println("Bad root token "
							+ currentTok.getTokenType());
							System.exit(0);
							break;
		}
	}

	/******************MAIN FUNCTIONAILITY*********************/
	
	public void parse(){
		root = new Subtree(i); 				//default constructor makes root

		if(i.hasNext())
			readNextTok();

		for(int j = 0; i.hasNext(); j++){

			if(currentTok != null)
				match();

			currentTok = root.children.get(j).token;
		}	

		root.printTree();
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

				// System.err.println(currentTok.getTokenType() + " " 
				// 	+ currentTok.getName());
			}
		}
		else 
			System.exit(0);
	}
	// public void dimWilds(){
	// 	wildCard();
	// 	x();
	// }

	// public void wildCard(){
	// 	//collapse
	// 	match("ASTERISK");
	// }

	// public void x(){
	// 	if(!currentTok.getTokenType().equals("COMMA")) return;

	// 	match("COMMA");
	// 	wildCard();
	// 	x();
	// }

	// public void fieldDeclaration(){
	// 	match("StringIdentifier");
	// 	typeDescriptor();
	// }

	// public void fieldDeclarations(){
	// 	fieldDeclaration();
	// 	y();
	// }

	// public void y(){
	// 	if(currentTok.getTokenType().equals("end")) return;

	// 	match("COMMA");
	// 	fieldDeclaration();
	// 	y();
	// }

	// public void dimensh(){
	// 	match("OPEN_BRACKET");
	// 	expressions();
	// 	match("CLOSE_BRACKET");
	// }
	// /*********************TESTERS**************************/

	// public boolean hasTypeDesc(){
	// 	//collapse
	// 	return !currentTok.getTokenType().equals("OPEN_BRACE");
	// }

	// public boolean hasParams(){
	// 	//collapse
	// 	return !currentTok.getTokenType().equals("CLOSE_PARENTHESIS");
	// }

	// public boolean isDimensh(){
	// 	//collapse
	// 	return currentTok.getTokenType().equals("OPEN_BRACKET");
	// }

	// public boolean isElse(){
	// 	//collapse
	// 	return currentTok.getTokenType().equals("else");
	// }

	// public boolean hasWilds(){
	// 	//collapse
	// 	return currentTok.getTokenType().equals("OPEN_BRACKET");
	// }


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
		//variable();
	} else if(currentTok.getTokenType().equals("LEFT_PARENTHESIS")) {
		match("LEFT_PARENTHESIS");
		if(currentTok.getTokenType().equals("PERIOD")) {
			//variable();
		} else {
			//funcCall();
		}
	}
}

public void typeCast() {
	match("LEFT_PARENTHESIS");
	expression();
	match("RIGHT_PARANTHESIS");
}

public void expression() {
	switch(currentTok.getTokenType()) {
		case "OPEN_PARENTHESIS":					//sub-expression
			match("OPEN_PARENTHESIS");
			expression();
			break;
		case "INT_IDENTIFIER":	//integer, ready for operator
			match("INT_IDENTIFIER");
			exprRest();
			break;
		case "FLOAT_IDENTIFIER":	//float, ready for operator
			//readNextToken();
			exprRest();
			break;
		case "BYTE_IDENTIFIER":	//byte, math expressions not allowed
			//readNextToken();
			//byteExpr();
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
	//isMatch = false;
	if(currentTok.getTokenType().equals("INT_IDENTIFIER")) {
		match("INT_IDENTIFIER");
	} else if (currentTok.getTokenType().equals("FLOAT_IDENTIFIER")) {
		match("FLOAT_IDENTIFIER");
	} else if(currentTok.getTokenType().equals("IDENTIFIER")) {
		match("IDENTIFIER");
	} else if (currentTok.getTokenType().equals("STRING_IDENTIFIER")) {
			match("STRING_IDENTIFIER");
	} else if(currentTok.getTokenType().equals("OPEN_PARENTHESIS")){
		match("OPEN_PARENTHESIS");
		expression();
	} else {
		//error();
	}
	return true;
}

public void exprRest() {
	//NEED SOME SORT OF STOPPING POINT OR BASE CASE

	//match to a math expression
	//read in the next part of the expression
	switch(currentTok.getTokenType()) {
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
			match("EXCLAMATION_POINT");
			matchOperand();
			exprRest();
			break;
		case "TILDE":
			match("TILDE");
			matchOperand();
			exprRest();
			break;
		case "MINUS":
			match("MINUS");
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

}