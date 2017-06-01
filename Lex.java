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
	private boolean windowsMachine = OSName.startsWith("Windows");		//true if OS is Windows
	private boolean unixMachine = OSName.startsWith("Linux");		//true if OS is Unix

	private Reader input;											//BufferedStream reader
	private OperatorMap opMap;										//language-specific Operators Map
	private KeywordMap kMap;										//language-specific Keyword Map
	private int col = 0, row = 1;									//track row and column of file
	private char currentChar = ' ', nextChar = ' ';					//track characters from input
	private boolean isKeyword = false;								//for digit / string eater
	private boolean processPending = false;							//unfinished currentChar processing
	private boolean commentBool = true;								//false if commentCheck found nothing
	private boolean readOk = true;									//input should read() new char

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
	* Character from input (BufferedReader) and processes the Character into a Token based on
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
		int column = col;
		int tempCol = col;

		if(processPending == true) {					//if true then a digit char must be processed
			returnToken = createDigit();
		} else {
			if(readOk && commentBool) {					//indicates Character should be read from input
	      		readNextChar();							//increments column
	      	}

	      	commentBool = true;							//can now keep reading
	      	readOk = true;								//can now keep reading

	      	if(isSpecialCase(currentChar)) {			//test special cases
	      		currentChar = (char)input.read();		//reads new cha

	      		if(Character.isLetter(currentChar) && col == 0)
	      			col++;

	      		processSpecial();						//processes all special
	      	}

	      	if(currentChar == '/'){						//possible comment
		      	returnToken = commentCheck();			//call the check

		      	if(returnToken != null){				//thus not a comment
		      		commentBool = false;				//dont read on next iter
		      		return returnToken;					//returning a BACKSLASH operator
		      	}
		      	//if returnToken == null, the comment has been read... safe to continue
	      	}

	      	if(opMap.operators.containsKey(String.valueOf(currentChar))) {	//cur in opMap?
	      		tempCol = col;

	      		if(isSpecialOperator()) {
	      			if(col == 0) {
	      				tempCol++;
	      				col++;
	      			}
	      			return new Op(opMap.operators.get(checkOperator()), row, tempCol);
	      		} else {
	      			return new Op(opMap.operators.get(String.valueOf(currentChar)), row, tempCol);
	      		}
	      	} else if(Character.isDigit(currentChar)) {				//checks if currentChar isDigit
	      		returnToken = createDigit();					//creates digit identifier
	      	} else if(Character.isLetter(currentChar)) {		//checks if currentChar isLetter

	      		returnToken = createStringIdentifier();			//either stringIdentifier or keyword

			}
			else if(currentChar == '"' || currentChar == '\'')	{
	
				returnToken = createStringIdentifier();
			}

		}

		//EXTREMELY SPECIAL CASE
		//Because createDigit() must eat one character ahead, if the last
		//line in the input stream is "var a = 55;", the semicolon will be 
		//eaten and lost by accident. Thus, if createDigit() has just been
		//called (meaning readOk is false), and input.ready() is false,
		// and the last character of the input stream is a semicolon...
		//  we must process that semicolon.  

		// I left the text file on that example, 
		// so Comment out the following if statement to see what I mean

		//this is easily the ugliest and least elegant solution I have 
		//ever created, if you have a better one, for the love of God, do it. 
		if(!input.ready() && !readOk && currentChar == ';'){
			leftOver = true;
			lastRow = row;
			lastCol = col;
		}

		return returnToken;
	}



	/**
	*
	* <h1>checkOperator()</h1>
	*
	* Method reads a new character from the BufferedReader and checks to see if
	* currentChar + nextChar are one of the two character operators in the Operators
	* map. Returns a string equal to the two character operator if the map lookup
	* is successful, original operator otherwise. If the two character operator is not
	* in the operators map, then the readOk is set to false, indicating that currentChar
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
		nextChar = (char)input.read();			//reads the nextChar from the BufferedReader
		increaseColumn(1);
		String retVal = "";									//string return value
		String lookup = Character.toString(currentChar) + 	//string to lookup = currentChar + nextChar
						Character.toString(nextChar);

		if(opMap.operators.get(lookup) != null) {			//checks if lookup is in the Operators Map
			retVal = lookup;								//sets retVal to lookup
		} else {											//otherwise the operator was only one char
			readOk = false;									//indicates currentChar  to be processed
			retVal = Character.toString(currentChar);		//retVal equal to operator
			currentChar = nextChar;
		}
		nextChar = ' ';										//resets nextChar
		return retVal;
	}

	/**
	*
	* <h1>isSpecialOperator()</h1>
	*
	* Method returns a boolean indicating whether currentChar matches any of the
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
		boolean isSpecial = false;
		if(currentChar == '<' || currentChar == '>' || currentChar == '&' ||
			currentChar == '=' || currentChar == '!' || currentChar == '|') {
			isSpecial = true;
		}
		return isSpecial;
	}

	/**
	*
	* <h1>processSpecial()</h1>
	*
	* Method continues reading Characters from BufferedReader until a Character is
	* encountered that is not a special case. See isSpecialCase() for special
	* case characters.
	* 
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/

	//destinys fix : comment out last line
	private void processSpecial() throws IOException {
		while(isSpecialCase(currentChar)) {
			currentChar = (char)input.read();		//continue reading until not special
		}
	}

	//JOHN:

	private boolean isSpecialCase(char cur)throws IOException {
		boolean specialCase = false;
		int tabW;

		tabW = (!windowsMachine) ? 8 : 4;			//fanciness

		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r') {		//special cases
			specialCase = true;												//return true
		
			switch(cur) {
				case ' ':	increaseColumn(1);
							break;
				case '\t':	increaseColumn(tabW);
							//System.out.println("tab");
							break;
				case '\n':	resetColumn();
							increaseRow();
							col++;
							//System.out.println("newline");
							break;
				default:	break;
			}
		}

		return specialCase;
	}

	/**
	*
	* <h1>commentCheck()</h1>
	*
	* If a comment found: return null, currentChar will be correct
	* If just a backslash: return a backslash, currentChar wont be correct
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
		nextChar = (char)input.read();	

	    if(nextChar == '*') {							//found a comment
	    	nextChar = ' ';
			comment();									//read and col++
	    	return null;								//no need to output '/'
	    } else { 										//no comment
	    	currentChar = nextChar;						//set new currentChar
	      	nextChar = ' ';								//reset nextChar
	      	return new Op("BACKSLASH", row, col++);		//return the BACKSLASH
	    }
	}

	/**
	*
	* <h1>comment()</h1>
	*
	* Preconditions: currentChar = '/', nextChar = '*'
	* Postconditions: currentChar = newCurrent; nextChar = ' ';
	* 
	* @return Token
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/

	//JOHN : initial increaseColumn only needs to be one, not two...
		//getNextT will take care of the first one
	private void comment() throws IOException {
		col++;
															//TODO: CHECK FOR /N
		while(true) {										//until a return 
			currentChar = (char)input.read();	

			if(!isSpecialCase(currentChar))					//if it was special the func()
				col++;										//will take care of col

			while(currentChar == '*'){						//possible exit
				currentChar = (char)input.read();			//read next

				if(!isSpecialCase(currentChar))
					col++;

				if(currentChar == '/'){						//exit
					readNextChar();
					return;									//done
				}
			}
		}
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
			convert += currentChar;								//append currentChar bc its a digit
			if(Character.isDigit(nextChar)) {					//check if nextChar is also a digit
				convert += nextChar;							//if so append to convert
				nextChar = ' ';									//reset nextChar
				readNextChar();										//increment col

				while(Character.isDigit(currentChar)) {			//while digit
					convert += currentChar;						//append char to convert
					readNextChar();								//increment col
					if(currentChar == '.') {					//checks for decimal number
						convert += currentChar;					//appends decimal to convert
						readNextChar();
						isInt = false;
					}
				}
			} else {											//otherwise nextChar is not a digit
				currentChar = nextChar;							//set currentChar to nextChar
				nextChar = ' ';									//reset nextChar
			}
			processPending = false;									//reset processPending
		} else {
			while(Character.isDigit(currentChar)) {				//while digit
				convert += currentChar;							//append char to convert
				readNextChar();								//increment col

				if(currentChar == '.') {					//checks for decimal number
					convert += currentChar;					//appends decimal to convert
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


	private void readNextChar()throws IOException{
		currentChar = (char)input.read();
		increaseColumn(1);
	}

	//JOHN: 
	//		ADDED readOK = false if a new stringIdentifier is created
	private Token createStringIdentifier() throws IOException {
		String result = "" + currentChar + "";
		String possibleKeyword = "";
		int tempCol = col;

		if(col == 0)
			tempCol++;

		if(currentChar == '"'){
			do{
				readNextChar();								//a, b, c
				result += currentChar;						//"abc"
			} while(currentChar != '"');

			return new StringLiteral(result, row, tempCol);
		}

		if(currentChar == '\'') {
			char cResult = currentChar;
			readNextChar();
			while (currentChar != '\'') {
				cResult = currentChar;
				readNextChar();
			}

			return new CharLiteral(cResult, row, tempCol);
		}
		
		readNextChar();


		//upon exiting while loop currentChar will NOT be equal to a letter
		while(Character.isLetter(currentChar)) {	//while currentChar is a letter
			result += currentChar;					//append currentChar to result
			readNextChar();							//increment col
		}

		//code below checks the cases for keywords that contain both letters and numbers
		//ex: int32, float64. Requires checking the two characters in the BufferedReader
		//that are past the end of the string (result) IFF the first character after the end of
		//the string is a digit.

		if(Character.isDigit(currentChar)){			//checks if currentChar is a digit
			possibleKeyword = result;				//sets possibleKeyword string to our result string						
			nextChar = (char)input.read();			//reads the nextChar from the BufferedReader
			increaseColumn(1);								//increments col
			//result += currentChar;

			//KEYWORD CASE: both currentChar and nextChar are digits
			//if true we must append currentChar and nextChar to possibleKeyword and check
			//to see if it matches any keys in the KeywordMap
			if(Character.isDigit(nextChar)) {
				possibleKeyword += currentChar;		//appends currentChar
				possibleKeyword += nextChar;		//appends nextChar
			}
			else if(!Character.isDigit(nextChar)){	//covers fib1 case
				result += currentChar;
				currentChar = nextChar;
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
				processPending = true;		//indicates that we must process currentChar & nextChar
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
		//byteRead opens input file in byte code	
		//cReader -> byte code to char code
		//input -> //char by char reading 
		try{
			FileInputStream byteRead = new FileInputStream(iFile); 					
			InputStreamReader cReader = new InputStreamReader(byteRead, "UTF8");	
			input = new BufferedReader(cReader);								
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
					return input.ready();				//ready checks stream readiness
				}catch(IOException e){
					e.printStackTrace();				//address of error
					return false;						//not return false
				}
			}

			@Override
			public Token next(){						//move to next char
				try{
					return getNextT();			//the next token method
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









