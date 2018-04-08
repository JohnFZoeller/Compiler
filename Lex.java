/**
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */

import java.util.*;
import java.io.*;

class Lex implements Iterable<Token> { 

	/************************************************************************************************
	*																								*
 	* 									Class data members											*
 	*																								*
 	************************************************************************************************/

	private String OSName = System.getProperty("os.name");			//stores OS name
	private boolean isWin = OSName.startsWith("Windows");	//true if OS is Windows
	private boolean unixMachine = OSName.startsWith("Linux");		//true if OS is Unix

	private Reader reader;											//BufferedStream reader
	private OperatorMap opMap;										//language-specific Operators Map
	private KeywordMap kMap;										//language-specific Keyword Map
	private int col = 0, row = 1;									//track row and column of file
	private char currentChar = ' ', nextChar = ' ';					//track characters from reader
	private boolean isKeyword = false;								//for digit / string eater
	private boolean processPending = false;							//unfinished this.currentChar processing
	private boolean readOk = true;									//reader should read() new char

	private boolean leftOver = false;
	private int lastRow, lastCol;


	/************************************************************************************************
	*																								*
 	* 										Private Methods											*
 	*																								*
 	************************************************************************************************/

	/**
	*
	* <h1>getNextT()</h1>
	*
	* getNextT() is the main processing method for the Lexical Analyzer. This method reads a
	* Character from reader (BufferedReader) and processes the Character into a Token based on
	* the Character's respective type. Returned Token's can be of type StringIdentifier,
	* DigitIdentifier, Keyword, or Operator.
	* 
	* @return Token
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/	


	//JOHN: LINE 104, LINE 129
	private Token getNextT() throws IOException{
		Token returnToken = null;						//return Token variable
		int column = col, tempCol = col;

		if(processPending == true) {					//if true then a digit char must be processed
			returnToken = createDigit();
		} else {
			if(readOk) readNextChar();							

	      	readOk = true;								//can now keep reading
	      	
	      	while(isEscapeChar()) parseEscape();		//should be after comment check 
	      
	      	if(this.currentChar == '/') {				//possible comment
		      	returnToken = commentCheck();			//token = next char after comment

		      	if(returnToken != null) {				//thus not a comment
		      		return returnToken;					//returning a BACKSLASH operator
		      	} //else : comment parsed
	      	}

	      	if(opMap.operators.containsKey(String.valueOf(this.currentChar))) {	//cur in opMap?
	      		tempCol = col;

	      		if(isSpecialOperator()) {
	      			if(col == 0) {
	      				tempCol++;
	      				col++;
	      			}
	      			return new Op(opMap.operators.get(checkOperator()), row, tempCol);
	      		} else {
	      			return new Op(opMap.operators.get(String.valueOf(this.currentChar)), row, tempCol);
	      		}
	      	} else if(Character.isDigit(this.currentChar)) {				//checks if this.currentChar isDigit
	      		returnToken = createDigit();					//creates digit identifier
	      	} else if(Character.isLetter(this.currentChar)) {		//checks if this.currentChar isLetter
	      		returnToken = createStringIdentifier();			//either stringIdentifier or keyword
			} else if(this.currentChar == '"' || this.currentChar == '\'')	{
				returnToken = createStringIdentifier();
			}

		}

		/* SPECIAL CASE
		 Because createDigit() must eat one character ahead, if the last
		 line in the reader stream is "var a = 55;", the semicolon will be 
		  eaten and lost by accident. Thus, if createDigit() has just been
		 called (meaning readOk is false), and reader.ready() is false,
		 and the last character of the reader stream is a semicolon...
		  we must process that semicolon.  
		*/
		if(!reader.ready() && !readOk && this.currentChar == ';'){
			leftOver = true;
			lastRow = row;
			lastCol = col;
		}

		return returnToken;
	}

	private void readNextChar() throws IOException{
		this.currentChar = (char)this.reader.read();
		increaseColumn(1);
	}

	/**
	*
	* <h1>checkOperator()</h1>
	*
	* Method reads a new character from the BufferedReader and checks to see if
	* this.currentChar + nextChar are one of the two character operators in the Operators
	* map. Returns a string equal to the two character operator if the map lookup
	* is successful, original operator otherwise. If the two character operator is not
	* in the operators map, then the readOk is set to false, indicating that this.currentChar
	* must be processed before reading a new character from the BufferedReader.
	* 
	* @return String
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/
	private String checkOperator() throws IOException {
		nextChar = (char)reader.read();			//reads the nextChar from the BufferedReader
		increaseColumn(1);
		String retVal = "";									//string return value
		String lookup = Character.toString(this.currentChar) + 	//string to lookup = this.currentChar + nextChar
						Character.toString(nextChar);

		if(opMap.operators.get(lookup) != null) {			//checks if lookup is in the Operators Map
			retVal = lookup;								//sets retVal to lookup
		} else {											//otherwise the operator was only one char
			readOk = false;									//indicates this.currentChar  to be processed
			retVal = Character.toString(this.currentChar);		//retVal equal to operator
			this.currentChar = nextChar;
		}
		nextChar = ' ';										//resets nextChar
		return retVal;
	}

	/**
	*
	* <h1>isSpecialOperator()</h1>
	*
	* Method returns a boolean indicating whether this.currentChar matches any of the
	* operators that have a special case. Special case is defined as an operator
	* that may exist as a single character, or may be concatenated with another
	* character to form a two character operator.
	* 
	* @return isSpecial
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/

	private boolean isSpecialOperator() {
		char c = this.currentChar;

		return (c == '<' || c == '>' || c == '&' ||
			c == '=' || c == '!' || c == '|');
	}

	private boolean isEscapeChar() throws IOException {
		char c = this.currentChar;

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
		char c = this.currentChar;
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

		this.currentChar = (char)this.reader.read();
	}

	/**
	*
	* <h1>commentCheck()</h1>
	*
	* If a comment found: return null, this.currentChar will be correct
	* If just a backslash: return a backslash, this.currentChar wont be correct
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
		this.currentChar = (char)this.reader.read();

	    if(this.currentChar == '*') {
			parseBlockComment();									
	    	return null;	
	    } else if(this.currentChar == '/') {	
	    	parseLineComment();
	    	return null;		
	    } else { 											  //no comment	
	      	this.readOk = false;						
	      	return new Op("BACKSLASH", this.row, this.col++); //return the BACKSLASH
	    }
	}

	/**
	* <h1>comment()</h1>
	* Preconditions: this.currentChar = '/', nextChar = '*'
	* Postconditions: this.currentChar = newCurrent; nextChar = ' ';
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
			this.currentChar = (char)this.reader.read();	//get char

			if(this.currentChar == '/' && last == '*') {	//exit
				readNextChar();
				return;										//done
			}

			while(isEscapeChar()) parseEscape();

			last = this.currentChar;
		}
	}

	//Preconditions		:	this.currentChar == '/'
	//Postconditions 	:   this.currentChar == the one after \n
	private void parseLineComment() throws IOException {
		while(this.currentChar != '\n') {
			this.currentChar = (char)this.reader.read();
			col++;
		}

		this.currentChar = (char)this.reader.read();
		col = 1; row++;
	}

	/**
	*
	* <h1>createDigit()</h1>
	*
	* Method reads Characters from the BufferedReader until a non-digit  is encountered.
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
		String convert = "";									//final int to string value
		boolean isInt = true;

		if(processPending == true) {							//thus atleast the curChar isDigit
			convert += this.currentChar;								//append this.currentChar bc its a digit
			if(Character.isDigit(nextChar)) {					//check if nextChar is also a digit
				convert += nextChar;							//if so append to convert
				nextChar = ' ';									//reset nextChar
				readNextChar();										//increment col

				while(Character.isDigit(this.currentChar)) {			//while digit
					convert += this.currentChar;						//append char to convert
					readNextChar();								//increment col
					if(this.currentChar == '.') {					//checks for decimal number
						convert += this.currentChar;					//appends decimal to convert
						readNextChar();
						isInt = false;
					}
				}
			} else {											//otherwise nextChar is not a digit
				this.currentChar = nextChar;							//set this.currentChar to nextChar
				nextChar = ' ';									//reset nextChar
			}
			processPending = false;									//reset processPending
		} else {
			while(Character.isDigit(this.currentChar)) {				//while digit
				convert += this.currentChar;							//append char to convert
				readNextChar();								//increment col

				if(this.currentChar == '.') {					//checks for decimal number
					convert += this.currentChar;					//appends decimal to convert
					readNextChar();
					isInt = false;
				}
			}
		}
		if(isInt) {
			result = Integer.parseInt(convert);
			readOk = false;
			return new IntIdentifier(result, row, column);
		} else {
			dResult = Float.parseFloat(convert);
			readOk = false;
			return new FloatIdentifier((float)dResult, row, column);
		}
	}

	//JOHN: 
	//		ADDED readOK = false if a new stringIdentifier is created
	private Token createStringIdentifier() throws IOException {
		String result = "" + this.currentChar + "";
		String possibleKeyword = "";
		int tempCol = col;

		if(col == 0)
			tempCol++;

		if(this.currentChar == '"'){
			do{
				readNextChar();								//a, b, c
				result += this.currentChar;						//"abc"
			} while(this.currentChar != '"');

			return new StringLiteral(result, row, tempCol);
		}

		if(this.currentChar == '\'') {
			char cResult = this.currentChar;
			readNextChar();
			while (this.currentChar != '\'') {
				cResult = this.currentChar;
				readNextChar();
			}

			return new CharLiteral(cResult, row, tempCol);
		}
		
		readNextChar();


		//upon exiting while loop this.currentChar will NOT be equal to a letter
		while(Character.isLetter(this.currentChar)) {	//while this.currentChar is a letter
			result += this.currentChar;					//append this.currentChar to result
			readNextChar();							//increment col
		}

		//code below checks the cases for keywords that contain both letters and numbers
		//ex: int32, float64. Requires checking the two characters in the BufferedReader
		//that are past the end of the string (result) IFF the first character after the end of
		//the string is a digit.

		if(Character.isDigit(this.currentChar)){			//checks if this.currentChar is a digit
			possibleKeyword = result;				//sets possibleKeyword string to our result string						
			nextChar = (char)reader.read();			//reads the nextChar from the BufferedReader
			increaseColumn(1);								//increments col
			//result += this.currentChar;

			//KEYWORD CASE: both this.currentChar and nextChar are digits
			//if true we must append this.currentChar and nextChar to possibleKeyword and check
			//to see if it matches any keys in the KeywordMap
			if(Character.isDigit(nextChar)) {
				possibleKeyword += this.currentChar;		//appends this.currentChar
				possibleKeyword += nextChar;		//appends nextChar
			}
			else if(!Character.isDigit(nextChar)){	//covers fib1 case
				result += this.currentChar;
				this.currentChar = nextChar;
				nextChar = ' ';
				readOk = false;
				return new StringIdentifier(result, row, tempCol);
			}

			//checks if the resulting string (possibleKeyword) matches a key in the KeywordMap
			//if true then we must return a new Keyword rather than a new stringIdentifier
			if(KeywordMap.keywords.containsKey(possibleKeyword)) {
				isKeyword = true;
				//readOk = false;
				return new Keyword(possibleKeyword, kMap.keywords.get(possibleKeyword), row, tempCol);
			} else {
				processPending = true;		//indicates that we must process this.currentChar & nextChar
				isKeyword = false;			//before reading new character from BufferedReader
			}
		}

		//if there were no digits, result could still be a keyword
		if(KeywordMap.keywords.containsKey(result)) {
			readOk = false;
			return new Keyword(result, KeywordMap.keywords.get(result), row, tempCol);
		}
		readOk = false;
		return new StringIdentifier(result, row, tempCol);
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









