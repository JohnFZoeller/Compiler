import java.util.*;
import java.io.*;

class FunctionSymbol extends ScopedSymbol implements Scope {
	FunctionSymbol(String n, SymbolType t, Scope e){
		super(n, t, e);
	}

	FunctionSymbol(String n, SymbolType t, Scope e, List<Subtree> p){
		super(n, t, e);
	}

	public void addParams(List<Subtree> params)throws AlreadyDefinedException, UndefinedTypeException{		
		for(int i = 0; i < params.size(); i++){
			Subtree nodeType = params.get(i).children.get(1);
			Symbol temp = paramType(nodeType, params.get(i).children.get(0).token.getName());
			define(temp);
		}

	}

	public Symbol paramType(Subtree nodeType, String sName)throws AlreadyDefinedException, UndefinedTypeException{
		SymbolType t;
		Symbol temp;

		if(nodeType instanceof NaTypeDescriptor){
			nodeType = nodeType.children.get(0);

			if(nodeType instanceof BasicType){
				t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return new Symbol(sName, t);
			}
			else if(nodeType instanceof Name){
				temp = resolve(nodeType.token.getName());

				if(temp == null)
					System.out.println("param type undefined");
				else{
					t = (temp.getType().getTypeName() == "record") ? 
						(RecordSymbol)temp.getType() : 
						(BuiltInTypeSymbol)temp.getType();

					return new Symbol(sName, t);
				}
			}
			else if(nodeType instanceof RecordDescriptor){
				RecordSymbol record = new RecordSymbol(sName, this, 
					nodeType.children.get(0).children);

				t = (RecordSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return new VarSymbol(sName, t, record, null);
			}
		}
		else if(nodeType instanceof Expression) {
			//handle ref lock here
			nodeType.decorateFirst(this);
			t = nodeType.type;
			return new VarSymbol(sName, t);
		}

		System.out.println("parse params error");
		return null;
	}

}