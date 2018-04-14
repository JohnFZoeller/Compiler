import java.io.*;
import java.util.*;

/**
* @ISSUE : LEXER NOT PERFECT : semicolons not read if on last line
**/
public class Main{
	public static void main(String[] args) {
		new Main().run("testin.txt");
	}

	public static void run(String in) {
		Lex lexer = new Lex(in);
		Iterator<Token> iter = lexer.iterator();

		printLexer(iter);

		//SyntaxParser par = new SyntaxParser(lexer);
		//mainTable contains the root GlobalScope, which is the bottom of the stack
		//of each of its childrens scope stacks : Page 134, 146

		//SymbolTable mainTable = new SymbolTable();	//move this?

		//par.parse();								//create the tree structure
		//par.decorate(mainTable);					//walk it
	}

	private static void printLexer(Iterator<Token> iter) {
		Token temp;

		while(iter.hasNext()) {
		  	temp = iter.next();

		 	if(temp != null) System.out.println(temp.toString());
		}

		System.out.println("\n DONE LEXING");
	}
}