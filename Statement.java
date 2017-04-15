// import java.util.*;
// import java.io.*;

// class ASTNode{
// 	protected String memAddress;
// 	protected ArrayList<ASTNode> children;
// 	protected int row, col;

// 	ASTNode(){
// 		memAddress = "";
// 		children = new ArrayList<ASTNode>();
// 		row = col = 0;
// 	}
// }

// class Parameters extends ASTNode{
// 	private ArrayList<Parameter> params;

// 	Parameters(){
// 		params = new ArrayList<Parameter>();
// 	}
// }

// class Parameter extends ASTNode{
// 	StringIdentifier name;
// 	//collapse
// }

// class BlockStatement extends ASTNode{
// 	private ArrayList<Statement> statements;

// 	public BlockStatement(){
// 		statements = new ArrayList<Statement>();
// 	}
// }

// class Statement extends ASTNode{
// 	protected int level = 0;
// 	protected Token t;
// 	String type;
// 	Statement left;
// 	Statement right;


// 	Statement() {
// 		super();
// 		row = 0;		//row = t.getRow();
// 		col = 0;		//col = setCol(t.getCol());
// 	}

// 	String printNode() {
// 		String retVal = "";
// 		String indent = "+--";

// 		for(int index = 0; index < level; index++) {
// 			retVal += indent;
// 		}
// 		retVal += /*(figure this out)*/ System.identityHashCode(this);
// 		retVal += type;
// 		return retVal;
// 	}
// }

// class Declaration extends Statement {
// 	protected StringIdentifier name;

// 	Declaration(StringIdentifier n) {
// 		super();
// 		name = n;
// 	}
// }

// 	class TypeDecl extends Declaration {
// 		TypeDescriptor type; 

// 		TypeDecl(StringIdentifier t){
// 			super(t);
// 		} 
// 	}

// 	class FuncDecl extends Declaration {
// 		//name, memAddress inherited from Declaration
// 		private Parameters params;
// 		private TypeDescriptor returnType;
// 		private BlockStatement body;

// 		public FuncDecl(StringIdentifier f){
// 			super(f);

// 		}
// 	}

// 	class VarDecl extends Declaration {
// 		public VarDecl(StringIdentifier v){
// 			super(v);
// 		}
// 	}

// 	class FieldDecl extends Declaration {
// 		public FieldDecl(StringIdentifier f){
// 			super(f);
// 		}
// 	}

// 	class Dimension extends Statement {
// 	}

// 	class ExprStatement extends Statement {
// 		ExprStatement right;
// 		Expression expr;
// 	}

// 		// LEAF NODE
// 		class Expression extends ExprStatement {
// 				//Variable var;
// 				//Operator op;
// 		}

// //class TypeCast extends 

// 		// class TypeDescriptor extends TypeDecl {

// 		// }

// 		// 	class NonArrayTypeDescrip extends TypeDescriptor {

// 		// 	}

// 		// 		class RecordDescrip extends NonArrayTypeDescrip {

// 		// 		}

// 		// 		class Identifier extends NonArrayTypeDescrip {

// 		// 		}

// 		// 		class BasicType extends NonArrayTypeDescrip {

// 		// 		}

// 		// 		// LEAF NODE
// 		// 		class BasicByte extends BasicType {

// 		// 		}

// 		// 		// LEAF NODE
// 		// 		class BasicInt extends BasicType {

// 		// 		}

// 		// 		// LEAF NODE
// 		// 		class BasicFloat extends BasicType {

// 		// 		}