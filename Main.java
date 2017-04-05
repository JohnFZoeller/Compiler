package tokens;

import java.io.*;
import java.util.*;


public class Main{
	public static void main(String[] args){
		new Main().run("test.txt");
	}
	public static void run(String in){
		Lex lexer = new Lex(in);
		Iterator<Token> iter = lexer.iterator();
		//temporary
		Token temp = iter.next();


		// while(iter.hasNext()){
		// 	temp = iter.next();

		// }


	}
}