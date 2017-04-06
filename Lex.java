/**
 * 
 *
 * @author Destiny Boyer
 * @author John Zoeller
 * @version 1.0
 *
 */

package tokens;
import java.util.*;
import java.io.*;

class Lex implements Iterable<Token> {
	private Reader input;								//final reading object
	private OperatorMap opMap;							//operators
	private KeywordMap kMap;							//keyword
	private int col = 0, row = 0;						//[row, col]
	private char currentChar = ' ', nextChar = ' ';		//characters trackers
	private boolean isKeyword = false;					//for digit / string eater
	private boolean processPending = false;			

	private boolean isSpecialCase(char cur){
		boolean specialCase = false;
		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r' || cur == '\b') {
			specialCase = true;
		}
		return specialCase;
	}

	private Token getNextT() throws IOException{
		boolean successfulRead = true;					//tracks if read is successful
		Token returnToken = null;						//return Token variable

		//if processPending == true then we know that nextChar has been filled
		//and that currentChar is a digit.
		if(processPending == true)	{					//if we still need to process currentChar & nextChar
			returnToken = createDigit();				//finish processing
		} else {

			if(successfulRead) {							//read first character
	      		currentChar = (char)input.read();			//cast to char
			}

			//at end of while loop currentChar will not be equal to a special case
	      	while(isSpecialCase(currentChar)) {				//test special cases
	      		currentChar = (char)input.read();			//eat until not special
	      		col++;										//increment col
	      	}

	      	//	CHECK FOR OPERATOR CASE HERE!!!!!!


	      	if(Character.isDigit(currentChar)) {			//checks if currentChar isDigit
	      		returnToken = createDigit();				//creates digit identifier
	      	}
	      	else if(Character.isLetter(currentChar)) {
	      		returnToken = createStringIdentifier();		//could be either stringIdentifier or keyword
			}
		}

		return returnToken;
	}

	private DigitIdentifier createDigit() throws IOException{
		int result;								//variable to hold final int value
		String convert = "";					//convert string int to int value

		if(processPending == true) {				//if we still need to look at currentChar & nextChar
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
		result = Integer.parseInt(convert);		//convert string representation of int to integer value
		return new DigitIdentifier(result, row, col);
	}

	private Token createStringIdentifier() throws IOException{
		String result = "", possibleKeyword = "";

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
				return new Keyword(possibleKeyword, KeywordMap.keywords.get(possibleKeyword), row, col);
			} else {
				processPending = true;		//indicates that we must process currentChar & nextChar
				isKeyword = false;			//before reading new character from BufferedReader
			}
		}
		//no need for this line in this else clause
		return new StringIdentifier(result, row, col);
	}

	public Lex(String iFile){
		try{
			FileInputStream byteRead = new FileInputStream(iFile); 				//opens input file in byte code		
			InputStreamReader cReader = new InputStreamReader(byteRead, "UTF8");	//converts byte code to char code
			input = new BufferedReader(cReader);								//char by char reading 
		}catch(IOException e){													//if file not found
			e.printStackTrace();												//print error at location
		}
	}

	@Override
	public Iterator<Token> iterator(){					//iterate buffered reader
		Iterator<Token> iter = new Iterator<Token>(){	//new iterator
			@Override
			public boolean hasNext(){					//reader has next
				try{
					return input.ready();					//ready checks stream readiness
				}catch(IOException e){
					e.printStackTrace();				//address of error
					return false;						//not return false
				}
			}
			@Override
			public Token next(){						//move to next char
				try{
					return getNextT();				//the next token method
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









