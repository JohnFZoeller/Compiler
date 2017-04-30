import java.io.*;
import java.util.*;

class SyntaxParser {
	private Token currentTok;
	private Iterator<Token> i;
	private boolean firstOne;

	public SyntaxParser(Lex lex){
		currentTok = null;
		i = lex.iterator();
		firstOne = true;
	}

	/*
		Main match function for the program. Logic is based off of the fact that the only
		time this match function will be called is in the case of a new statement in the program.
		Function matches currentTok's token type
	*/

	public void match() {
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
		if(firstOne){
			readNextTok();					//reads next Token into currentTok
			firstOne = false;
		}

		match();							//matches currentTok to a statement type, toInsert is
		parse();
	}

	public void match(String expect){
		if(currentTok.getTokenType().equals(expect))
			readNextTok();
		else throw new 
			Error("Expected: " + expect + "Got: " + currentTok.getTokenType());
	}

	public void readNextTok() {
		if(i.hasNext())
			currentTok = i.next();
		else 
			System.exit(0);

		if(currentTok != null)
			System.err.println(currentTok.getTokenType());
	}

	/******************STATEMENT TYPES*************************/

	public void forr(){
		match("for");

		match("OPEN_PARENTHESIS");
		expresh();
		match("SEMICOLON");
		expresh();
		match("SEMICOLON");
		expresh();
		match("CLOSE_PARENTHESIS");

		block();
	}

	public void whilee(){
		match("while");

		match("OPEN_PARENTHESIS");
		expresh();
		match("CLOSE_PARENTHESIS");

		block();
	}

	public void iff(){
		match("if");

		match("OPEN_PARENTHESIS");
		expresh();
		match("CLOSE_PARENTHESIS");

		block();

		if(isElse()){
			match("else");
			block();
		}
	}

	public void print(){
		match("print");

		expresh();

		match("SEMICOLON");
	}		

	public void retur(){
		match("return");

		if(isExpresh())
			expresh();

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
			expresh();								//parse it

		match("SEMICOLON");
	}

	public void func(){
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
			expresh();
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
			expresh();
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

	public void fieldDeclaration(){
		match("StringIdentifier");
		typeDescriptor();
	}

	public void dimensh(){
		match("OPEN_BRACKET");
		expreshs();
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

	public boolean isExpresh(){
		//return true if currentTok is an expression
		return true;
	}

	public void expresh(){
		//collapse
		match("StringIdentifier");
	}

	public void expreshs(){}

	/***********************RECURSIONS**************************/

	public void fieldDeclarations(){}

	public void params(){
		//collapse
		readNextTok();
	}

	public void dimWilds(){}
}