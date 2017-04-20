import java.io.*;
import java.util.*;

class SyntaxParser {
	private Token currentTok;
	private ASTNode toInsert;
	private Boolean parseOk;
	private Iterator<Token> i;

	public SyntaxParser(Lex lex){
		currentTok = null;
		toInsert = null;
		parseOk = false;

		i = lex.iterator();
	}

	/*
		Main match function for the program. Logic is based off of the fact that the only
		time this match function will be called is in the case of a new statement in the program.
		Function matches currentTok's token type and sets the ASTNode toInsert to a new Node
		of the appropriate type.
	*/

	public void match() {

		switch(currentTok.getTokenType()) {
			case "for":		toInsert = new For();
							break;
			case "while":	toInsert = new While();
							break;
			case "if":		toInsert = new If();
							break;
			case "print":	toInsert = new Print();
							break;
			case "return":	toInsert = new Return();
							break;
			case "type":	toInsert = new Declaration();
							break;
			case "exit":	toInsert = new Exit();
							break;
			case "func":	toInsert = new Declaration();
							break;
			case "var":		toInsert = new VarDecl(i);
							break;
			case "static":	toInsert = new VarDecl();
							break;
			default:		break;
		}
	}

	
		// Function is called after match(). The BufferedReader is passed to toInsert via the function
		// call parse(tokenReader). This function call is when toInsert will use the tokenReader to
		// parse tokens in order to populate it's data members. toInsert.parse() is successful then
		// it means that toInsert should be inserted into the Tree, otherwise it will be set to null
		// as the tokens were not good data.

		// Function sets toInsert to null so that it can again be used to create a new Node for the
		// next statement read from the BufferedReader.
	

	public boolean populate() {
		//boolean success = toInsert.parser(); return success;
		return toInsert.parser();
	}

	public void readNextTok() {
		currentTok = i.next();

		if(currentTok != null){
			System.err.println(currentTok.getTokenType());
		}

		//parseOk = (i.hasNext()) ? true : false;
	}

	public Token getNextToken(){
		if(i.hasNext())
			readNextTok();

		return currentTok;
	}

	/*
		Main functionality for SyntaxParser. Recursively calls itself as long as there is still good
		data from the BufferedReader.
	*/

	public void parse(){
		readNextTok();						//reads next Token into currentTok
		match();							//matches currentTok to a statement type, toInsert is
											//set to new Node of the appropriate type
		boolean success = false;			//passes BufferedReader to toInsert to populate it's
		success = populate();				//data members. Sets success to populate()'s return value

		if(success){						//if toInsert was able to successfully populate it's data members
			//syntaxTree.insert(toInsert);	//insert toInsert into our AST
		}

		if(parseOk) {						//if there are still Tokens to read from tokenReader
			parse();						//recursively call parse to continue populating the AST
		}
	}

	//aka : getParseOk
	public Boolean canParse(){
		return parseOk;
		//collapse
	}

	//aka : setParseOk
	public void setParse(Boolean p){
		parseOk = p;
		//collapse
	}





}