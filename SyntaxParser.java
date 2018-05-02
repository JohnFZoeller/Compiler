import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class SyntaxParser {
	private int row, col;
	private String indent;
	private Token currentTok;
	private Subtree root;
	private Iterator<Token> i;
	private List<String> constantValues;
	private List<String> subroutines;


	public SyntaxParser(Lex lexer){
		this.currentTok = null;
		this.i = lexer.iterator();
		this.indent = "";
		this.row = this.col = 0;
		this.constantValues = new ArrayList<String>();
		this.subroutines = new ArrayList<String>();
	}

	public void parse(){
		this.root = new Subtree(this.i);

		if(i.hasNext()) readNextTok();

		for(int j = 0; i.hasNext(); j++){
			if(this.currentTok != null) match();

			currentTok = root.children.get(j).token;
		}	

		System.out.println('\n');
		root.printTree();
		System.out.println('\n');
	}

	public void match(){
		switch(currentTok.getTokenType()) {
			case "for":		root.addChild(new For(currentTok, i));
							break;
			case "while":	root.addChild(new While(currentTok, i));
							break;
			case "if":		root.addChild(new If(currentTok, i));
							break;
			case "print":	root.addChild(new Print(currentTok, i));
							break;
			case "return":	root.addChild(new Retur(currentTok, i));
							break;
			case "type":	root.addChild(new Type(currentTok, i));
							break;
			case "exit":	root.addChild(new Exit(currentTok, i));
							break;
			case "function":root.addChild(new Function(currentTok, i));
							break;
			case "var":		root.addChild(new Var(currentTok, i));
							break;
			case "static":	root.addChild(new Var(currentTok, i));
							break;
			case "const":	root.addChild(new Var(currentTok, i));
							break;
			case "StringIdentifier":
							Expression toAdd = new Expression(currentTok, i);
							root.addChild(toAdd);
							currentTok = toAdd.token;
							if(currentTok.getTokenType().equals("SEMICOLON"))
								match("SEMICOLON");
							break;
			default:		System.out.println("Bad root token "
							+ currentTok.getTokenType());
							System.exit(0);
							break;
		}
	}

	public void match(String expect){
		if(currentTok.getTokenType().equals(expect)){
			readNextTok();
		}
		else throw new 
			Error("Expected: " + expect + "Got: " + currentTok.getTokenType());
	}

	public void readNextTok() {
		if(i.hasNext()){
			currentTok = i.next();
			// if(currentTok != null)
				// System.err.println(currentTok.getTokenType() + " " 
				// 	+ currentTok.getName());
		}
		else throw new Error("Parsers readNextTok failed ");
	}

	public void decorate(SymbolTable mainTable) {

		//Bernsteins recommendation for how to decorate
		//0th pass = lexing and parsing							-> already done
		//1st pass = type/var/func declaration statements (all declarations)
			//Think these would be identifiers	-> ie. use resolve()
		//2nd pass = initializers, statements, function bodies
			//these would by symbols -> ie. use define()

		//throw new Error("terminated");
		root.beginDecorateFirst(mainTable);
		root.beginDecorateSecond(mainTable);
		root.emitAssemblyCode(constantValues, subroutines);
	}

}