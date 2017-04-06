package tokens;
import java.util.*;
import java.io.*;

class Lex implements Iterable<Token>{
	private Reader input;
	private Token current, next;
	private OperatorMap opMap;
	private KeywordMap kMap;
	private int col = 0, row = 0;
	private char currentChar = ' ', nextChar = ' ';
	private boolean isKeyword = false;

	private boolean isSpecialCase(char  cur){
		if(cur == ' ' || cur == '\n' || cur == '\t' || cur == '\r' || cur == '\b')
			return false;
		else return true;
	}

	private Token getNextT() throws IOException{
		boolean successRead = true;
		currentChar = ' ';

		if(input.ready() == false)						//istream ready?
			throw new IOException();

		if(successRead) 								//read first token
      		currentChar = (char)input.read();			//cast to char

      	while(isSpecialCase(currentChar))				//test special cases
      		currentChar = (char)input.read();			//eat until not special

      	col++;											//go to next char col

      	if(Character.isDigit(currentChar))
      		return createDigit();
      	else if(Character.isLetter(currentChar)) {
      		Token retVal = createStringIdentifier();
      		if(isKeyword == false) {								//process current, process next
      																//before reseting current to (char)input.read()

      		}
      		else{										//is a keyword identifier object
      			//need to reset current and next 
      		}
		}



		return new Token(1,2);
	}

	private DigitIdentifier createDigit(){
		int result, temp;
		String convert = "";

		while(Character.isDigit(currentChar)){
			convert += currentChar;
			currentChar = (char)input.read();
			col++;
		}

		result = Integer.parseInt(convert);


		return new DigitIdentifier(result, row, col);
	}

	private StringIdentifier createStringIdentifier() {
		String result = "";
		String temp = "";
		while(Character.isLetter(currentChar)) {
			result += currentChar;
			currentChar = (char)input.read();
			col++;
		}
		if(Character.isDigit(currentChar)) {
			temp = result;
			next = (char)input.read();
			col++;
			if(Character.isDigit(next)) {
				temp += currentChar + next;
			}

			if(KeywordMap.containsKey(temp)) {
				isKeyword = true;
				return new Keyword(temp, KeywordMap.get(temp));
			} else {
				isKeyword = false;
				return new StringIdentifier(result);
			}
		}

		return new StringIdentifier(result);
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









