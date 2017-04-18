//package tokens;

import java.io.*;
import java.util.*;


public class Main{
	public static void main(String[] args){
		new Main().run("testin.txt");
	}
	public static void run(String in){
		Lex lexer = new Lex(in);
		Iterator<Token> iter = lexer.iterator();
		SyntaxParser par = new SyntaxParser();
		Token temp;


		while(iter.hasNext() && par.canParse()){
			temp = iter.next();
			System.out.println(temp.getTokenType());


			//System.err.println(temp);
		}

		// if(lexer.leftOverSemi()){
		// 	temp = new Op("SEMICOLON", lexer.getLastRow(), lexer.getLastCol());
		// 	System.err.println(temp);
		// }

	}
}