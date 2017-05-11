import java.io.*;
import java.util.*;

public class Main{
	public static void main(String[] args){
		//collapse
		new Main().run("testin.txt");
	}

	public static void run(String in){
		Lex lexer = new Lex(in);
		Iterator<Token> iter = lexer.iterator();
		SyntaxParser par = new SyntaxParser(lexer);
		ScopeTree rootScope = new ScopeTree();

		par.parse();			//create the tree structure
		par.decorate(rootScope);	//walk it



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