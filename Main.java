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
		ArrayList<Token> toks = new ArrayList<Token>();
		ASTTree parser;

		while(iter.hasNext()){
			temp = iter.next();
			toks.add(temp);
			System.err.println(temp);
		}

		if(lexer.leftOverSemi()){
			temp = new Op("SEMICOLON", lexer.getLastRow(), lexer.getLastCol());
			toks.add(temp);
			System.err.println(temp);
		}

		parser = new ASTTree(toks);
		parser.printTree();
	}
}