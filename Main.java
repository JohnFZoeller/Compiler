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
		Token temp;

		par.parse();
<<<<<<< HEAD
		// Token temp;
=======
		//Token temp;
>>>>>>> 3043d57994be9213351099be1f5d1b197e2db37c
		// while(iter.hasNext()){
		//  	temp = iter.next();

		//  	if(temp != null)
		//  		System.out.println(temp.getTokenType());
		//  }

	}
}

/*-------Please dont delete below, still need to debug Lexer--------*/

// if(lexer.leftOverSemi()){
// 	temp = new Op("SEMICOLON", lexer.getLastRow(), lexer.getLastCol());
// 	System.err.println(temp);
// }