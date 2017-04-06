package tokens;
import java.util.*;
import java.io.*;

class Lex implements Iterable<Token>{
	private Reader input;								//final reading object
	private OperatorMap opMap;							//operators
	private KeywordMap kMap;							//keyword
	private int col = 0, row = 0;						//[row, col]
	private char currentChar = ' ', nextChar = ' ';		//characters trackers
	private boolean isKeyword = false;					//for digit / string eater			

	private boolean isSpecialCase(char cur){
		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r' || cur == '\b')
			return false;
		else return true;
	}

	private Token getNextT() throws IOException{
		boolean successRead = true;

		// if(input.ready() == false)					//dont need this,
		// 	throw new IOException();					//already in the iterator overload

		if(successRead) 								//read first token
      		currentChar = (char)input.read();			//cast to char

      	while(isSpecialCase(currentChar))				//test special cases
      		currentChar = (char)input.read();			//eat until not special

      	col++;											//go to next char col

      	if(Character.isDigit(currentChar))
      		return createDigit();
      	else if(Character.isLetter(currentChar)) {
      		Token retVal = createStringIdentifier();	//could be either stringIdentifier or keyword
      		// if(isKeyword == false) {					//process current, process next
      		// 											//before reseting current to (char)input.read()
      		// }
      		// else{										//is a keyword identifier object
      		// 	//need to reset current and next 
      		// }
		}



		return new Token(1,2);
	}

	private DigitIdentifier createDigit() throws IOException{
		int result, temp;
		String convert = "";					//54673

		while(Character.isDigit(currentChar)){	//while digit
			convert += currentChar;				//5, 4, 6, 7, 3
			currentChar = (char)input.read();	//4, 6, 7, 3
			col++;
		}

		result = Integer.parseInt(convert);		//to int
		return new DigitIdentifier(result, row, col);
	}

	private Token createStringIdentifier() throws IOException{
		String result = "", temp = "";

		while(Character.isLetter(currentChar)) {	//read in word
			result += currentChar;
			currentChar = (char)input.read();
			col++;
		}

		if(Character.isDigit(currentChar)){			//could be keyword so read nums
			temp = result;						
			nextChar = (char)input.read();
			col++;

			if(Character.isDigit(nextChar))
				temp += currentChar + nextChar;

			if(KeywordMap.keywords.containsKey(temp)) {
				isKeyword = true;
				return new Keyword(temp, KeywordMap.keywords.get(temp), row, col);
			} else {
				isKeyword = false;
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









