/**
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 
 */

import java.util.*;
import java.io.*;

class Lex implements Iterable<Token> { 

	/**************************************************************************
	*
 	* 							Class data members
 	*	
 	***************************************************************************/

	private String OSName = System.getProperty("os.name");
	private boolean isWin = OSName.startsWith("Windows");

	private Reader reader;
	private OperatorMap opMap;
	private KeywordMap kMap;
	private int col = 0, row = 1;						//track row/col of file
	private char currChar = ' ', nextChar = ' ';		//chars from reader
	private boolean readOk = true;						//reader should read()

	private boolean leftOver = false;
	private int lastRow, lastCol;


	/*************************************************************************
	*
 	* 								Private Methods
 	*
 	**************************************************************************/

	/**
	*
	* <h1>getNextT()</h1>
	*
	* getNextT() is the main processing method for the Lexical Analyzer. Reads a
	* Character from reader and processes the Character into a Token based on
	* the Character's respective type. Returned Token's can be of type
	* DigitIdentifier, Keyword, or Operator, etc...
	* 
	* @return Token
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/	
	private Token getNextT() throws IOException{
		Token returnToken = null;						//return Token variable
		int column = col, tempCol = col;
		String op;


		if(readOk) readNextChar();							

      	readOk = true;								//can now keep reading
      	
      	while(isEscapeChar()) parseEscape();		//after comment check?
      
      	if(this.currChar == '/') {					//possible comment
	      	returnToken = commentCheck();			//token = char after comment

	      	if(returnToken != null) {				//thus not a comment
	      		return returnToken;					//returning a BACKSLASH
	      	} //else : comment parsed
      	}

      	if(opMap.operators.containsKey(op = String.valueOf(this.currChar))) {
      		tempCol = col;

      		if(isSpecialOperator()) {
      			if(col == 0) { tempCol++; col++; }

      			return new Op(opMap.operators.get(doubleOp()), row, tempCol);
      		} else {
      			return new Op(opMap.operators.get(op), row, tempCol);
      		}
      	} else if(Character.isDigit(this.currChar)) {
      		returnToken = createDigit();					//creates digitID
      	} else if(Character.isLetter(this.currChar)) {
      		returnToken = createStringIdentifier();			//stringId or keyword
		} else if(this.currChar == '"' || this.currChar == '\'')	{
			returnToken = createStringIdentifier();
		}

		/* SPECIAL CASE
		 Because createDigit() must eat one character ahead, if the last
		 line in the reader stream is "var a = 55;", the semicolon will be 
		  eaten and lost by accident. Thus, if createDigit() has just been
		 called (meaning readOk is false), and reader.ready() is false,
		 and the last character of the reader stream is a semicolon...
		  we must process that semicolon. reader.mark(x) reader.reset 
		*/
		if(!reader.ready() && !readOk && this.currChar == ';'){
			//reader.mark(1);

		}

		return returnToken;
	}

	private void readNextChar() throws IOException{
		this.currChar = (char)this.reader.read();
		increaseColumn(1);
	}

	/**
	* <h1>doubleOp()</h1>
	*
	* Method reads a new character from the BufferedReader and checks to see if
	* currChar + next are one of the two character operators in the Operators
	* map. Returns a string to the two character operator if the map lookup
	* is successful, original operator otherwise. If two character op is not
	* in the operators map, then the readOk is set to false, thus currChar
	* must be processed before reading a new character from the BufferedReader.
	* 
	* @return String
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	* @todo eliminate next char replace with last char
	*/
	private String doubleOp() throws IOException {
		col++;
		this.nextChar = (char)this.reader.read();		//reads the nextChar
		String retVal = "";								//string return val
		String lookup = String.valueOf(this.currChar) + //doubleOperator
						String.valueOf(this.nextChar);

		if(opMap.operators.containsKey(lookup)) {		//lookup exists eg-> ==
			retVal = lookup;							//sets retVal to lookup
		} else {										//eg ->  =a
			readOk = false;								//freeze input reader
			retVal = String.valueOf(this.currChar);		//retVal = singleOp
			this.currChar = this.nextChar;				//update currChar
		}

		nextChar = ' ';									//resets nextChar
		return retVal;
	}

	/**
	* <h1>isSpecialOperator()</h1>
	*
	* Method returns a boolean indicating whether this.currChar matches any
	* operators that may exist as a single character, or may be concatenated 
	* with another character to form a two character operator.
	* 
	* @return isSpecial
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/
	private boolean isSpecialOperator() {
		char c = this.currChar;

		return (c == '<' || c == '>' || c == '&' ||
			c == '=' || c == '!' || c == '|');
	}

	private boolean isEscapeChar() throws IOException {
		char c = this.currChar;

		return (c == ' ' || c == '\n' || c == '\t' || c == '\r');
	}

	/**
	* <h1>parseEscape()</h1>
	* 
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*/
	private void parseEscape() throws IOException {
		char c = this.currChar;
		int tabW = (!this.isWin) ? 8 : 4;

		switch(c) {
				case ' ':	increaseColumn(1);
							break;
				case '\t':	increaseColumn(tabW);
							break;
				case '\n':	resetColumn();
							this.row++;
							this.col++; break;
				default:	break; }

		this.currChar = (char)this.reader.read();
	}

	/**
	*
	* <h1>commentCheck()</h1>
	*
	* If a comment found: return null, this.currChar will be correct
	* If just a backslash: return a backslash, this.currChar wont be correct
	*	Created a bool to fix this problem. The backslash will return from
	*	getNextT(), thus completing the current iteration. The next iteration
	*	will not be allowed to read a new character because this function
	*	was forced to read it already to check for a comment. 
	* 
	* @return Token
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/
	private Token commentCheck() throws IOException {
		this.currChar = (char)this.reader.read();

	    if(this.currChar == '*') {
			parseBlockComment();									
	    	return null;	
	    } else if(this.currChar == '/') {	
	    	parseLineComment();
	    	return null;		
	    } else { 											  //no comment	
	      	this.readOk = false;						
	      	return new Op("BACKSLASH", this.row, this.col++); //return the BACKSLASH
	    }
	}

	/**
	* <h1>comment()</h1>
	* Preconditions: this.currChar = '/', nextChar = '*'
	* Postconditions: this.currChar = newCurrent; nextChar = ' ';
	* 
	* @return Token
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	* @TODO     : 	handle syntax error -> /*/
	
	private void parseBlockComment() throws IOException {
		char last = 'i';									//temp valu

		while(this.reader.ready()) {						//until a return or EOF
			this.currChar = (char)this.reader.read();	//get char

			if(this.currChar == '/' && last == '*') {	//exit
				readNextChar();
				return;										//done
			}

			while(isEscapeChar()) parseEscape();

			last = this.currChar;
		}
	}

	//Preconditions		:	this.currChar == '/'
	//Postconditions 	:   this.currChar == the one after \n
	private void parseLineComment() throws IOException {
		while(this.currChar != '\n') {
			this.currChar = (char)this.reader.read();
			col++;
		}

		this.currChar = (char)this.reader.read();
		col = 1; row++;
	}

	/**
	* <h1>createDigit()</h1>
	*
	* Method reads Characters from the BufferedReader until a non-digit
	* A new DigitIdentifier is then created and returned.
	* 
	* @return DigitIdentifier
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/

	private Token createDigit() throws IOException {
		int result, column = col;
		double dResult;
		String stringNum = "";
		boolean isInt = true;

		while(Character.isDigit(this.currChar)) {
			stringNum += this.currChar;
			readNextChar();

			if(this.currChar == '.') {				//checks for decimal number
				stringNum += this.currChar;			//appends decimal to stringN
				readNextChar();						//prep for next iter
				isInt = false;
			}
		}

		this.readOk = false;

		if(isInt) {
			result = Integer.parseInt(stringNum);
			return new IntIdentifier(result, row, column);
		} else {
			dResult = Float.parseFloat(stringNum);
			return new FloatIdentifier((float)dResult, row, column);
		}
	}

	private Token createStringIdentifier() throws IOException {
		String result = "" + this.currChar + "";
		String possibleKey = "", val = "";

		if(this.currChar == '"') return createStringLiteral();
		if(this.currChar == '\'') return createCharLiteral();

		readNextChar();

		while(Character.isLetter(this.currChar)) {
			result += this.currChar;				//append currChar to result
			readNextChar();							//increment col
		}

		if(Character.isDigit(this.currChar)){		//eg a keyword = int32
			possibleKey = result;					
			this.nextChar = (char)reader.read();	//reads nextChar from reader
			increaseColumn(1);

			//currChar and nextChar are digits - append and check existence
			if(Character.isDigit(this.nextChar)) {
				possibleKey += this.currChar;	
				possibleKey += this.nextChar;
				this.nextChar = ' ';		

				//sorry future John
				if(!Objects.equals(null, val = kMap.keywords.get(possibleKey)))
					return new Keyword(possibleKey, val, row, col);
				return new StringIdentifier(possibleKey, row, col);
			} else {
				result += this.currChar;
				this.currChar = nextChar;
				nextChar = ' ';
				readOk = false;
				return new StringIdentifier(result, row, col);
			}
		}

		this.readOk = false;		//no digits? result could still be a keyword

		if(kMap.keywords.containsKey(result))
			return new Keyword(result, kMap.keywords.get(result), row, col);
		return new StringIdentifier(result, row, col);
	}

	private Token createStringLiteral() throws IOException {
		String result = "\"" + "";

		do{
			readNextChar();								//a, b, c
			result += this.currChar;					//"abc"
		} while(this.currChar != '"');

		return new StringLiteral(result, this.row, this.col);
	}

	//@todo: 'asdfa' is obviously an invalid char.
	//but this parser fails to recognize that
	private Token createCharLiteral() throws IOException {
		char cResult = this.currChar;

		readNextChar();
		while (this.currChar != '\'') {
			cResult = this.currChar;
			readNextChar();
		}

		return new CharLiteral(cResult, this.row, this.col);
	}

	//JOHN: changed to set to 0. 
	private void resetColumn() {
		this.col = 0;
	}

	private void increaseColumn(int count) {
		this.col = this.col + count;
	}

	private void increaseRow() {
		this.row = this.row + 1;
	}

	/************************************************************************************************
	*																								*
 	* 										Public Methods											*
 	*																								*
 	************************************************************************************************/

	public Lex(String iFile) {
		//byteRead opens reader file in byte code	
		//cReader -> byte code to char code
		//reader -> //char by char reading 
		try{
			FileInputStream byteRead = new FileInputStream(iFile); 					
			InputStreamReader cReader = new InputStreamReader(byteRead, "UTF8");	
			reader = new BufferedReader(cReader);								
		}catch(IOException e){							//if file not found
			e.printStackTrace();						//print error at location
		}
	}

	@Override
	public Iterator<Token> iterator() {					//iterate buffered reader
		Iterator<Token> iter = new Iterator<Token>(){	//new iterator

			@Override
			public boolean hasNext(){					//reader has next
				try{
					return reader.ready();				//ready checks stream readiness
				}catch(IOException e){
					e.printStackTrace();				//address of error
					return false;						//not return false
				}
			}

			@Override
			public Token next(){						//move to next char
				try{
					return getNextT();					//the next token method
				}catch(IOException e){
					e.printStackTrace();				//address of error
					return null;						//else return null
				}
			}

			@Override
			public void remove(){
				//this is temporary, need to implement
				throw new UnsupportedOperationException();
			}
		};
		return iter;									//return the instance
	}

	public boolean leftOverSemi(){
		return leftOver;
	}

	public int getLastRow(){
		return lastRow;
	}

	public int getLastCol(){
		return lastCol;
	}


}









