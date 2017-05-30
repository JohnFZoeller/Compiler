import java.util.*;
import java.io.*;

class FunctionSymbol extends ScopedSymbol implements Scope {
	FunctionSymbol(String n, SymbolType t, Scope e){
		super(n, t, e);
	}

	FunctionSymbol(String n, SymbolType t, Scope e, List<Subtree> p){
		super(n, t, e);
	}

	public void addParams(List<Subtree> params)throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		boolean defaultValuesLocked = false;

		for(int i = 0; i < params.size(); i++){
			Subtree nodeType = params.get(i).children.get(1);

			if(nodeType instanceof Expression){
				defaultValuesLocked = true;
			}
			else if(nodeType instanceof NaTypeDescriptor){
				if(defaultValuesLocked){
					System.out.println("Parameter Syntax Error-> If a formal parameter is assigned a default value, all parameters to its right must also have default values.");
					break;
				}
			}

			ParamSymbol temp = paramType(nodeType, 
				params.get(i).children.get(0).token.getName(), ((Param)params.get(i)).locks);
			define(temp);
		}

	}

	public ParamSymbol paramType(Subtree nodeType, String sName, boolean [] pLocks)throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		SymbolType t;
		Symbol temp;

		if(nodeType instanceof NaTypeDescriptor){
			nodeType = nodeType.children.get(0);

			if(nodeType instanceof BasicType){
				t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return new ParamSymbol(sName, t, pLocks);
			}
			else if(nodeType instanceof Name){
				temp = resolve(nodeType.token.getName());

				if(temp == null)
					System.out.println("param type undefined");
				else{
					t = (temp.getType().getTypeName() == "record") ? 
						(RecordSymbol)temp.getType() : 
						(BuiltInTypeSymbol)temp.getType();

					return new ParamSymbol(sName, t, pLocks);
				}
			}
			else if(nodeType instanceof RecordDescriptor){
				RecordSymbol record = new RecordSymbol(sName, this, 
					nodeType.children.get(0).children);

				t = (RecordSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return new ParamSymbol(sName, t, record, pLocks);
			}
		}
		else if(nodeType instanceof Expression) {
			nodeType.decorateFirst(this);
			t = nodeType.type;
			return new ParamSymbol(sName, t, pLocks);
		}

		System.out.println("parse params error");
		return null;
	}

}