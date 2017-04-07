/**
 * 
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */

//package tokens;
import java.util.*;
import java.io.*;

class Lex implements Iterable<Token> {
	private Reader input;								//final reading object
	private OperatorMap opMap;							//operators
	private KeywordMap kMap;							//keyword
	private int col = 0, row = 1;						//[row, col]
	private char currentChar = ' ', nextChar = ' ';		//characters trackers
	private boolean isKeyword = false;					//for digit / string eater
	private boolean processPending = false;
	private boolean commentBool = true;					//set false if commentCheck found nothing
	private boolean readOk = true;							//tracks if read is successful


	private Token getNextT() throws IOException{
		Token returnToken = null;						//return Token variable

		//if (processPending) next char is filled and curChar isDigit
		//might have to add commentBool to the below case, for now leave it
		//TODO: since createDigit() reads input, its possible that we could 
		//read something by accident when the commentBool flag is restricting reads. 
		if(processPending == true) {					//must process currentChar & nextChar
			returnToken = createDigit();				//finish processing
		} else {

			if(readOk && commentBool)					//read first character
	      		currentChar = (char)input.read();		//cast to char

	      	commentBool = true;							//can now keep reading
	      	readOk = true;								//can now keep reading

	      	while(isSpecialCase(currentChar)) {			//test special cases
	      		currentChar = (char)input.read();		//eat until not special
	      		//col++;									//increment col
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

			switch(currentChar){
	      		case '(': return new Op(opMap.operators.get("("), row, ++col);
	      		case ')': return new Op(opMap.operators.get(")"), row, ++col);
	      		case '[': return new Op(opMap.operators.get("["), row, ++col);
	      		case ']': return new Op(opMap.operators.get("]"), row, ++col);
	      		case '{': return new Op(opMap.operators.get("{"), row, ++col);
	      		case '}': return new Op(opMap.operators.get("}"), row, ++col);
	      		case ',': return new Op(opMap.operators.get(","), row, ++col);
	      		case ';': return new Op(opMap.operators.get(";"), row, ++col);
	      		case '+': return new Op(opMap.operators.get("+"), row, ++col);
	      		case '-': return new Op(opMap.operators.get("-"), row, ++col);
	      		case '~': return new Op(opMap.operators.get("~"), row, ++col);
	      		case '^': return new Op(opMap.operators.get("^"), row, ++col);
	      		case '*': return new Op(opMap.operators.get("*"), row, ++col);
	      		case '<': return new Op(opMap.operators.get(checkOperator()), row, col);
	      		case '>': return new Op(opMap.operators.get(checkOperator()), row, col);
	      	    case '&': return new Op(opMap.operators.get(checkOperator()), row, col);
	      	    case '|': return new Op(opMap.operators.get(checkOperator()), row, col);
	      	   	case '=': return new Op(opMap.operators.get(checkOperator()), row, col);
	      	   	case '!': return new Op(opMap.operators.get(checkOperator()), row, col);

	      		default: break;
	      	}

	      	if(Character.isDigit(currentChar)) {			//checks if currentChar isDigit
	      		returnToken = createDigit();				//creates digit identifier
	      	}
	      	else if(Character.isLetter(currentChar)) {
	      		returnToken = createStringIdentifier();		//either stringIdentifier or keyword
			}


		}

		return returnToken;
	}

	private String checkOperator() throws IOException{
		nextChar = (char)input.read();
		col++;

		String retVal;
		String lookup = Character.toString(currentChar) + 
						Character.toString(nextChar);

		if(opMap.operators.get(lookup) != null){
			retVal = lookup;

			if(col != 1)
				col++;

		}else{
			readOk = false;
			retVal = Character.toString(currentChar);
			currentChar = nextChar;
		}

		nextChar = ' ';
		return retVal;
	}


	//If a comment found: return null, currentChar will be correct
	//If just a backslash: return a backslash, currentChar wont be correct
		//Created a bool to fix this problem. The backslash will return from
		//getNext, thus completing the current iteration. The next iteration
		//will not be allowed to read a new character because this function
		//was forced to read it already to check for a comment. 
	private Token commentCheck() throws IOException{
		nextChar = (char)input.read();					//check next for *				

	    if(nextChar == '*') {							//found a comment
	    	comment();									//read and col++
	    	return null;								//no need to output '/'
	    }
	    else{ 											//no comment
	    	currentChar = nextChar;						//set new currentChar
	      	nextChar = ' ';								//reset nextChar
	      	return new Op("BACKSLASH", row, ++col);		//return the BACKSLASH
	    }
	}

	//preconditions: currentChar = '/', nextChar = '*'
	//postconditions: currentChar = newCurrent; nextChar = ' ';
	private void comment() throws IOException{
		nextChar = ' ';									//reset nextChar
														//TODO: CHECK FOR /N

		while(true){									//until a return 
			col++;										//increment line
			currentChar = (char)input.read();			//read


			while(currentChar == '*'){					//possible exit
				currentChar = (char)input.read();		//read next

				if(currentChar == '/'){					//exit
					currentChar = (char)input.read();	//jump char forward
					return;								//done
				}
			}

		}

	}

	//there is a tiny little bug in these two (that i didnt hunt down due to selfishness)
		//that will eat one extra character from the stream... see the output screenshot
	private DigitIdentifier createDigit() throws IOException{
		int result;									//final int value
		String convert = "";						//final int to string value

		if(processPending == true) {				//thus atleast the curChar isDigit
			convert += currentChar;					//append currentChar since we know it is a digit
			if(Character.isDigit(nextChar)) {		//check if nextChar is also a digit
				convert += nextChar;				//if so append to convert
				nextChar = ' ';						//reset nextChar
				currentChar = (char)input.read();	//read next char from BufferReader
				col++;								//increment col

				while(Character.isDigit(currentChar)) {	//while digit
					convert += currentChar;				//append char to convert
					currentChar = (char)input.read();	//read next char from BufferedReader
					col++;								//increment col
				}

			} else {								//otherwise nextChar is not a digit
				currentChar = nextChar;				//set currentChar to nextChar
				nextChar = ' ';						//reset nextChar
			}
		processPending = false;						//reset processPending
		} else {
			while(Character.isDigit(currentChar)){	//while digit
				convert += currentChar;				//append char to convert
				currentChar = (char)input.read();	//read next char from BufferedReader
				col++;								//increment col
			}
		}
		result = Integer.parseInt(convert);		
		return new DigitIdentifier(result, row, col);
	}

	private Token createStringIdentifier() throws IOException{
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

	private boolean isSpecialCase(char cur){
		boolean specialCase = false;

		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r') {
			specialCase = true;

			if(cur == '\n' || cur == '\r'){
				row++;
				col = 0;
			}
			else if(cur == ' '){
				col++;
			}
			else if(cur == '\t'){
				col = col + 4;
			}

		}
		return specialCase;
	}

	public Lex(String iFile){
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
	public Iterator<Token> iterator(){					//iterate buffered reader
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









