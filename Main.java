import java.io.*;
import java.util.*;

public class Main{
	public static void main(String[] args) throws UndefinedTypeException, AlreadyDefinedException {
		//collapse
		new Main().run("testin.txt");
	}

	public static void run(String in) throws UndefinedTypeException, AlreadyDefinedException {
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

/*-------Please dont delete below, still need to debug Lexer--------*/

// if(lexer.leftOverSemi()){
// 	temp = new Op("SEMICOLON", lexer.getLastRow(), lexer.getLastCol());
// 	System.err.println(temp);
// }