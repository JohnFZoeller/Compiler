	// class Statement extends ASTNode{
	// 	protected int level = 0;
	// 	Statement left;
	// 	Statement right;

	// 	Statement() {
	// 		super();
	// 	}

	// 	String printNode() {
	// 		String retVal = "";
	// 		String indent = "+--";

	// 		for(int index = 0; index < level; index++) {
	// 			retVal += indent;
	// 		}
	// 		retVal += /*(figure this out)*/ System.identityHashCode(this);
	// 		retVal += type;
	// 		return retVal;s
	// 	}
	// }

	// 	/****************************************************
	// 					BLOCK-STATEMENT
	// 	*****************************************************/

	// 	class BlockStatement extends Statement{
	// 		//private ArrayList<Statement> statements;

	// 		public BlockStatement(){
	// 			//statements = new ArrayList<Statement>();
	// 		}
	// 	}

	// 	/****************************************************
	// 					DECLARATION-STATEMENT
	// 	*****************************************************/

	// 	class Declaration extends Statement {
	// 		protected StringIdentifier name;

	// 		Declaration(StringIdentifier n) {
	// 			super();
	// 			name = n;
	// 		}

	// 		Declaration(){
	// 			super();
	// 		}

	// 		public void setName(StringIdentifier n){
	// 			name = n;
	// 		}
	// 	}

	// 		class TypeDecl extends Declaration {
	// 			TypeDescriptor type; 

	// 			TypeDecl(StringIdentifier t){
	// 				super(t);
	// 			} 
	// 		}

	// 		class FuncDecl extends Declaration {
	// 			//name, memAddress inherited from Declaration
	// 			//private Parameters params;
	// 			private TypeDescriptor returnType;
	// 			private BlockStatement body;

	// 			public FuncDecl(StringIdentifier f){
	// 				super(f);
	// 			}
	// 		}

	// 		class VarDecl extends Declaration {

	// 		//variable-declaration ::=
 //  				// staticopt constopt var identifier type-descriptor ;
 //  				// staticopt constopt var identifier = expression ;

	// 			public VarDecl(StringIdentifier v){
	// 				super(v);
	// 			}

	// 			public VarDecl(){
	// 				super();
	// 				parser();

	// 			}

	// 			public void parser(){
	// 				System.out.println("here");
	// 				Token currentToken;
	// 				return;
	// 			}
	// 		}

	// 		class FieldDecl extends Declaration {
	// 			public FieldDecl(StringIdentifier f){
	// 				super(f);
	// 			}
	// 		}

	// 	/****************************************************
	// 					FOR-STATEMENT
	// 	*****************************************************/

	// 	class For extends Statement{
	// 		private Expression init;
	// 		private Expression test;
	// 		private Expression incr;
	// 		private BlockStatement body;

	// 		public For(){
	// 			super();
	// 		}
	// 	}

	// 	/****************************************************
	// 					WHILE-STATEMENT
	// 	*****************************************************/

	// 	class While extends Statement{
	// 		private Expression test;
	// 		private BlockStatement body;

	// 		public While(){
	// 			super();
	// 		}
	// 	}

	// 	***************************************************
	// 					IF-STATEMENT
	// 	****************************************************

	// 	class If extends Statement{
	// 		private Expression test;
	// 		private BlockStatement body;

	// 		public If(){
	// 			super();
	// 		}
	// 	}

	// 	/****************************************************
	// 					PRINT-STATEMENT
	// 	*****************************************************/

	// 	class Print extends Statement{
	// 		private Expression out;

	// 		public Print(){
	// 			super();
	// 		}
	// 	}

	// 	/****************************************************
	// 					RETURN-STATEMENT
	// 	*****************************************************/

	// 	class Return extends Statement{
	// 		private Expression optional;

	// 		public Return(){
	// 			super();
	// 			optional = null;
	// 		}
	// 	}

	// 	/****************************************************
	// 					EXIT-STATEMENT
	// 	*****************************************************/

	// 	class Exit extends Statement{
	// 		private Expression optional;

	// 		public Exit(){
	// 			super();
	// 			optional = null;
	// 		}
	// 	}

