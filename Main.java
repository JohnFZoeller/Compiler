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
		SyntaxParser par = new SyntaxParser(lexer);

		//from page 134, 146
		//mainTable contains the root GlobalScope, which is the bottom of the stack
		//of each of its childrens scope stacks
		SymbolTable mainTable = new SymbolTable();

		par.parse();				//create the tree structure
		par.decorate(mainTable);	//walk it



		// Token temp;
		// while(iter.hasNext()){
		//   	temp = iter.next();

		//  	if(temp != null)
		//   		System.out.println(temp.getTokenType() + "    " + temp.getName());
		// }
	}
}