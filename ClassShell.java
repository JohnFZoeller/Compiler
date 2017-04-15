
// class Statement {
// 	int level = 0;
// 	String type;
// 	Statement left;
// 	Statement right;

// 	StatementNode() {

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
// 	Declaration() {

// 	}
// }

// 	class TypeDecl extends Declaration {
// 		TypeDescriptor type;  
// 	}

// 		class TypeDescriptor extends TypeDecl {

// 		}

// 			class NonArrayTypeDescrip extends TypeDescriptor {

// 			}

// 				class RecordDescrip extends NonArrayTypeDescrip {

// 				}

// 				class Identifier extends NonArrayTypeDescrip {

// 				}

// 				class BasicType extends NonArrayTypeDescrip {

// 				}

// 				// LEAF NODE
// 				class BasicByte extends BasicType {

// 				}

// 				// LEAF NODE
// 				class BasicInt extends BasicType {

// 				}

// 				// LEAF NODE
// 				class BasicFloat extends BasicType {

// 				}

// 	class FuncDecl extends Declaration {

// 	}

// 	class VarDecl extends Declaration {

// 	}

// 	class FieldDecl extends Declaration {

// 	}

// 	class Dimension extends Statement {

// 	}

// 	class ExprStatement extends Statement {
// 		ExprStatement right;
// 		Expression expr;
// 	}

// 		// LEAF NODE
// 		class Expression extends ExprStatement {
// 				Variable var;
// 				Operator op;
// 		}

// class TypeCast extends 