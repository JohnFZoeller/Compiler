	// TODO:

	// createDigit()

	// TODO: THINK ABOUT DOUBLE AND FLOATING POINT NUMBERS. THIS METHOD DOES NOT ACCOUNT
	// FOR NUMBERS THAT HAVE A DECIMAL PLACE. ALSO NEED TO THINK ABOUT HOW TO HANDLE
	// NUMBERS THAT MAY BE SPLIT ACROSS TWO ROW (people do weird shit... you never know)
	
	// comment()

	// TODO: HANDLING COMMENTS ACROSS MULTIPLE LINES


/**
 * 
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */


/**
 * Imports java util library and io library
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
	private boolean windowsMachine = OSName.startsWith("Windows");	//true if OS is Windows
	private boolean unixMachine = OSName.startsWith("Unix");		//true if OS is Unix

	private Reader input;											//BufferedStream reader
	private OperatorMap opMap;										//language-specific Operators Map
	private KeywordMap kMap;										//language-specific Keyword Map
	private int col = 0, row = 1;									//track row and column of file
	private char currentChar = ' ', nextChar = ' ';					//track characters from input
	private boolean isKeyword = false;								//for digit / string eater
	private boolean processPending = false;							//indicates unfinished currentChar processing
	private boolean commentBool = true;								//set false if commentCheck found nothing
	private boolean readOk = true;									//indicates if input should read() new char


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

	private Token getNextT() throws IOException{
		Token returnToken = null;						//return Token variable

		//this block indicates that currentChar needs to be processed
		//before another Character is read from the BufferedReader
		//currentChar is guaranteed to be a digit

		//TODO: since createDigit() reads input, its possible that we could 
		//read something by accident when the commentBool flag is restricting reads. 

		if(processPending == true) {					//processes currentChar
			returnToken = createDigit();
		} else {
			if(readOk && commentBool) {					//indicates Character should be read from input
	      		currentChar = (char)input.read();		//reads char from BufferedReader
	      	}

	      	commentBool = true;							//can now keep reading
	      	readOk = true;								//can now keep reading

	      	if(isSpecialCase(currentChar)) {			//test special cases
	      		processSpecial();
	      	}

	      	if(currentChar == '/'){						//possible comment
		      	returnToken = commentCheck();			//call the check

		      	if(returnToken != null){				//thus not a comment
		      		commentBool = false;				//see below comment
		      		//tells the next iteration not to read a new currentChar, no need
		      		return returnToken;					//returning a BACKSLASH operator
		      	}
		      	//if it did return null that means the comment has been read,
		      	//current char will be set correctly and its safe to continue 
	      	}

	      	//checks if currentChar matches any of the operators in the Operators Map
	      	if(opMap.operators.containsKey(String.valueOf(currentChar))) {
	      		if(isSpecialOperator()) {
	      			return new Op(opMap.operators.get(checkOperator()), row, col);
	      		} else {
	      			return new Op(opMap.operators.get(String.valueOf(currentChar)), row, ++col);
	      		}
	      	} else if(Character.isDigit(currentChar)) {				//checks if currentChar isDigit
	      		returnToken = createDigit();					//creates digit identifier
	      	} else if(Character.isLetter(currentChar)) {		//checks if currentChar isLetter
	      		returnToken = createStringIdentifier();			//either stringIdentifier or keyword
			}
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
		nextChar = (char)input.read();						//reads next Character from BufferedReader
		col++;												//increments col
		String retVal = "";									//string return value
		String lookup = Character.toString(currentChar) + 	//string to lookup, equal to currentChar + nextChar
						Character.toString(nextChar);
		if(opMap.operators.get(lookup) != null) {			//checks if lookup is in the Operators Map
			retVal = lookup;								//sets retVal to lookup
			if(col != 1){									//increments col if we are not in a new row
				col++;
			}
		} else {											//otherwise the operator was only one character
			readOk = false;									//indicates currentChar still needs to be processed
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

	private void processSpecial() throws IOException {
		while(isSpecialCase(currentChar)) {
			currentChar = (char)input.read();		//continue reading until not special
		}
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
		nextChar = (char)input.read();					//check next for *				

	    if(nextChar == '*') {							//found a comment
	    	comment();									//read and col++
	    	return null;								//no need to output '/'
	    } else { 											//no comment
	    	currentChar = nextChar;						//set new currentChar
	      	nextChar = ' ';								//reset nextChar
	      	return new Op("BACKSLASH", row, ++col);		//return the BACKSLASH
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

	private void comment() throws IOException {
		nextChar = ' ';										//reset nextChar
															//TODO: CHECK FOR /N
		while(true) {										//until a return 
			col++;											//increment line
			currentChar = (char)input.read();				//read


			while(currentChar == '*'){						//possible exit
				currentChar = (char)input.read();			//read next

				if(currentChar == '/'){						//exit
					currentChar = (char)input.read();		//jump char forward
					return;									//done
				}
			}
		}
	}

	/**
	*
	* <h1>createDigit()</h1>
	*
	* Method reads Characters from the BufferedReader until a non-digit Character is encountered.
	* A new DigitIdentifier is then created and returned.
	* 
	* @return DigitIdentifier
	* @throws IOException
	* @author Destiny Boyer
	* @author John Zoeller
	* @version %G%
	*
	*/

	private DigitIdentifier createDigit() throws IOException {
		int result;												//final int value
		String convert = "";									//final int to string value
		if(processPending == true) {							//thus atleast the curChar isDigit
			convert += currentChar;								//append currentChar since we know it is a digit
			if(Character.isDigit(nextChar)) {					//check if nextChar is also a digit
				convert += nextChar;							//if so append to convert
				nextChar = ' ';									//reset nextChar
				currentChar = (char)input.read();				//read next char from BufferReader
				col++;											//increment col

				while(Character.isDigit(currentChar)) {			//while digit
					convert += currentChar;						//append char to convert
					currentChar = (char)input.read();			//read next char from BufferedReader
					col++;										//increment col
				}
			} else {											//otherwise nextChar is not a digit
				currentChar = nextChar;							//set currentChar to nextChar
				nextChar = ' ';									//reset nextChar
			}
		processPending = false;									//reset processPending
		} else {
			while(Character.isDigit(currentChar)) {				//while digit
				convert += currentChar;							//append char to convert
				currentChar = (char)input.read();				//read next char from BufferedReader
				col++;											//increment col
			}
		}
		result = Integer.parseInt(convert);		
		return new DigitIdentifier(result, row, col);
	}

	private Token createStringIdentifier() throws IOException {
		String result = "", possibleKeyword = "";
		int tempCol = col;

		if(col == 0)
			tempCol++;

		//upon exiting while loop currentChar will NOT be equal to a letter
		while(Character.isLetter(currentChar)) {	//while currentChar is a letter
			result += currentChar;					//append currentChar to result
			currentChar = (char)input.read();		//read next char from BufferedReader
			col++;									//increment col
		}

		//code below checks the cases for keywords that contain both letters and numbers
		//ex: int32, float64. Requires checking the two characters in the BufferedReader
		//that are past the end of the string (result) IFF the first character after the end of
		//the string is a digit.

		if(Character.isDigit(currentChar)){			//checks if currentChar is a digit
			possibleKeyword = result;				//sets possibleKeyword string to our result string						
			nextChar = (char)input.read();			//reads the nextChar from the BufferedReader
			col++;									//increments col

			// ASSUMPTIONS:
			// we know that if nextChar is filled, then currentChar was a digit

			//KEYWORD CASE: both currentChar and nextChar are digits
			//if true we must append currentChar and nextChar to possibleKeyword and check
			//to see if it matches any keys in the KeywordMap
			if(Character.isDigit(nextChar)) {
				possibleKeyword += currentChar;		//appends currentChar
				possibleKeyword += nextChar;		//appends nextChar
			}

			//checks if the resulting string (possibleKeyword) matches a key in the KeywordMap
			//if true then we must return a new Keyword rather than a new stringIdentifier
			if(KeywordMap.keywords.containsKey(possibleKeyword)) {
				isKeyword = true;
				return new Keyword(possibleKeyword, kMap.keywords.get(possibleKeyword), row, tempCol);
			} else {
				processPending = true;		//indicates that we must process currentChar & nextChar
				isKeyword = false;			//before reading new character from BufferedReader
			}
		}

		//if there were no digits, result could still be a keyword
		if(KeywordMap.keywords.containsKey(result))
			return new Keyword(result, KeywordMap.keywords.get(result), row, col);

		return new StringIdentifier(result, row, col);
	}

	private boolean isSpecialCase(char cur)throws IOException {
		boolean specialCase = false;

		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r') {
			specialCase = true;
		
			if(windowsMachine) {
				if(cur == '\r') {							//in Windows machines \r\n is for newlines
					currentChar = (char)input.read();		//know that we must eat up the next character
					if(currentChar == '\n') {
						row++;
						col = 0;
					}
				}
			} else if(unixMachine) {
				if(cur == '\n' || cur == '\r'){
					row++;
					col = 0;
				}
			}

			if(cur == ' ') {
				col++;
			} else if(cur == '\t') {
				col = col + 4;
			}
		}

		return specialCase;
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

}









