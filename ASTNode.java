import java.io.*;
import java.util.*;

class ASTNode{
		protected String memAddress;
		protected int row, col;
		protected ArrayList<Token> tokens;
		protected String type;

		public ASTNode(){
			memAddress = "";
			row = col = 0;
			tokens = null;
			type = "";
		}

		public ASTNode(ArrayList<Token> t){
			memAddress = "";
			tokens = t;
			row = col = 0;
			type = "";
		}

		public Boolean hasChildren(){
			return (!tokens.isEmpty());
		}

		private void setRunningCol(int c){
			col += c;
		}
	}

	class Statement extends ASTNode{
		protected int level = 0;
		Statement left;
		Statement right;

		Statement() {
			super();
		}

		String printNode() {
			String retVal = "";
			String indent = "+--";

			for(int index = 0; index < level; index++) {
				retVal += indent;
			}
			retVal += /*(figure this out)*/ System.identityHashCode(this);
			retVal += type;
			return retVal;
		}
	}
	

		/****************************************************
						BLOCK-STATEMENT
		*****************************************************/

		class BlockStatement extends Statement{
			//private ArrayList<Statement> statements;

			public BlockStatement(){
				//statements = new ArrayList<Statement>();
			}
		}

		/****************************************************
						DECLARATION-STATEMENT
		*****************************************************/

		class Declaration extends Statement {
			protected StringIdentifier name;

			Declaration(StringIdentifier n) {
				super();
				name = n;
			}

			Declaration(){
				super();
			}

			public void setName(StringIdentifier n){
				name = n;
			}
		}

			class TypeDecl extends Declaration {
				TypeDescriptor type; 

				TypeDecl(StringIdentifier t){
					super(t);
				} 
			}

			class FuncDecl extends Declaration {
				//name, memAddress inherited from Declaration
				//private Parameters params;
				private TypeDescriptor returnType;
				private BlockStatement body;

				public FuncDecl(StringIdentifier f){
					super(f);
				}
			}

			class VarDecl extends Declaration {
			//variable-declaration ::=
  				// staticopt constopt var identifier type-descriptor ;
  				// staticopt constopt var identifier = expression ;

				public VarDecl(StringIdentifier v){
					super(v);
				}

				public VarDecl(){
					super();
					parser();

				}

				public void parser(){
					System.out.println("here");
					Token currentToken;
					return;
				}
			}

			class FieldDecl extends Declaration {
				public FieldDecl(StringIdentifier f){
					super(f);
				}
			}

		/****************************************************
						FOR-STATEMENT
		*****************************************************/

		class For extends Statement{
			private Expression init;
			private Expression test;
			private Expression incr;
			private BlockStatement body;

			public For(){
				super();
			}
		}

		/****************************************************
						WHILE-STATEMENT
		*****************************************************/

		class While extends Statement{
			private Expression test;
			private BlockStatement body;

			public While(){
				super();
			}
		}

		/****************************************************
						IF-STATEMENT
		*****************************************************/

		class If extends Statement{
			private Expression test;
			private BlockStatement body;

			public If(){
				super();
			}
		}

		/****************************************************
						PRINT-STATEMENT
		*****************************************************/

		class Print extends Statement{
			private Expression out;

			public Print(){
				super();
			}
		}

		/****************************************************
						RETURN-STATEMENT
		*****************************************************/

		class Return extends Statement{
			private Expression optional;

			public Return(){
				super();
				optional = null;
			}
		}

		/****************************************************
						EXIT-STATEMENT
		*****************************************************/

		class Exit extends Statement{
			private Expression optional;

			public Exit(){
				super();
				optional = null;
			}
		}

		/****************************************************
						EXPRESSION-STATEMENT
		*****************************************************/

		// class Expr extends Statement {
		// 	private Token value;			//string literal
		// 	private ExprRest rest;			//rest of the expression

		// 	public Expr(){
		// 		super();
		// 	}

		// 	public boolean parser(BufferedReader input) {
		// 		boolean successful = false;
		// 		Token currentTok = (Token)input.read();
				
		// 		match(currentTok);
		// 		rest = new ExprRest();
		// 		successful = rest.parse(input);
		// 		return successful;
		// 	}

		// 	public boolean match(Token toCompare) {
		// 		boolean match = false;
		// 		if(toCompare instanceof StringIdentifier) { 
		// 			value = toCompare;
		// 		} else {
		// 			//throw new ParseError();
		// 		}
		// 	}
		// }

		// class ExprRest extends Expr {
		// 	private Op operation;
		// 	private ExprRest rest;

		// 	public ExprRest(){
		// 		super();
		// 	}

		// 	public boolean parser(BufferedReader input) {
		// 		boolean successful = false;
		// 		Token currentTok = (Token)input.read();
		// 		match(currentTok);

		// 		if(operation == ')' || operation == ';') {	//end of the expression
		// 			successful = true;
		// 		} else {
		// 			rest = new ExprRest();
		// 			successful = rest.parse(input);
		// 		}
		// 		return successful;
		// 	}

		// 	public boolean match(Token toCompare) {
		// 		boolean match = false;
		// 		if(toCompare instanceof Op) {
		// 			operation = toCompare.getTokenType();
		// 		} else {
		// 			//throw new ParseError();
		// 		}
		// 	}
		// }
	

	class Dimension extends ASTNode {
		private ArrayList<Expression> exprs;

		public Dimension(){
			super();

		}
	}

	class Parameters extends ASTNode{
		private ArrayList<Parameter> params;

		Parameters(){
			params = new ArrayList<Parameter>();
		}
	}

	class Descriptor extends ASTNode{
		NonArrayTypeDescrip current;
		Dimension arrayDimension;
	}

	





	// class NonArrayTypeDescrip {

	// }

	// 	class RecordDescrip extends NonArrayTypeDescrip {
	// 		Keyword record;
	// 		Keyword end;


	// 	}

	// 	class Identifier extends NonArrayTypeDescrip {

	// 	}

	// 	class BasicType extends NonArrayTypeDescrip {

	// 	}
