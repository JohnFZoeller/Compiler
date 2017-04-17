class SyntaxParser {
	private Token currentTok;
	private ASTNode toInsert;
	private BufferedReader tokenReader;
	private ASTTree syntaxTree;

	/*
		Main match function for the program. Logic is based off of the fact that the only
		time this match function will be called is in the case of a new statement in the program.
		Function matches currentTok's token type and sets the ASTNode toInsert to a new Node
		of the appropriate type.
	*/

	public void match() {
		switch(currentTok.getTokenType()) {
			case "for":		toInsert = new ForNode();
							break;
			case "while":	toInsert = new WhileNode();
							break;
			case "if":		toInsert = new IfNode();
							break;
			case "print":	toInsert = new PrintNode();
							break;
			case "return":	toInsert = new ReturnNode();
							break;
			case "type":	toInsert = new DeclNode();
							break;
			case "exit":	toInsert = new ExitNode();
							break;
			case "func":	toInsert = new DeclNode();
							break;
			case "var":		toInsert = new DeclNode();
							break;
			default:		toInsert = new ExprNode();
							break;
		}
	}

	/*
		Function is called after match(). The BufferedReader is passed to toInsert via the function
		call parse(tokenReader). This function call is when toInsert will use the tokenReader to
		parse tokens in order to populate it's data members. toInsert.parse() is successful then
		it means that toInsert should be inserted into the Tree, otherwise it will be set to null
		as the tokens were not good data.

		Function sets toInsert to null so that it can again be used to create a new Node for the
		next statement read from the BufferedReader.
	*/

	public boolean populate() {
		boolean success = toInsert.parse(tokenReader)
		return success;
	}

	public void readNextTok() {
		currentTok = (Token)tokenReader.read();
	}

	/*
		Main functionality for SyntaxParser. Recursively calls itself as long as there is still good
		data from the BufferedReader.
	*/

	public void parse() {
		readNextTok();						//reads next Token into currentTok
		match();							//matches currentTok to a statement type, toInsert is
											//set to new Node of the appropriate type
		boolean success = false;			//passes BufferedReader to toInsert to populate it's
		success = populate();				//data members. Sets success to populate()'s return value

		if(success) {						//if toInsert was able to successfully populate it's data members
			syntaxTree.insert(toInsert);	//insert toInsert into our AST
		}

		if(tokenReader.ready()) {			//if there are still Tokens to read from tokenReader
			parse();						//recursively call parse to continue populating the AST
		}
	}

}