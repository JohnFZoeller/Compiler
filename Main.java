import java.util.Iterator;

public class Main{
	public static void main(String[] args) {
		new Main().run("testin.txt");
	}

	public static void run(String in) {
		Lex lexer = new Lex(in);
		Iterator<Token> iter = lexer.iterator();
		SymbolTable symbolTable = new SymbolTable();
		SyntaxParser parser = new SyntaxParser(lexer);

		parser.parse();								//create the tree structure
		parser.decorate(symbolTable);				//walk it	
	}

	private static void printLexer(Iterator<Token> iter) {
		Token temp;

		while(iter.hasNext()) {
		  	temp = iter.next();

		 	if(temp != null) System.out.println(temp.toString());
		}

		System.out.println("\n DONE LEXING\n\n");
		//mainTable contains the root GlobalScope, which is bottom of the stack
		//of each of its childrens scope stacks : Page 134, 146
	}
}