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
		Token temp;

		while(iter.hasNext()){
			temp = iter.next();
			System.out.println(temp);
		}

	}
}