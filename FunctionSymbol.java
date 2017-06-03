import java.util.*;
import java.io.*;

class FunctionSymbol extends ScopedSymbol implements Scope {
	ArraySymbol array;
	RecordSymbol record;

	FunctionSymbol(String n, SymbolType t, Scope e){
		super(n, t, e);
	}

	FunctionSymbol(String n, SymbolType t, Scope e, List<Subtree> p){
		super(n, t, e);
	}

	FunctionSymbol(String n, SymbolType t, Scope e, ArraySymbol a){
		super(n, t, e);
		array = a;
	}

	FunctionSymbol(String n, SymbolType t, Scope e, RecordSymbol r){
		super(n, t, e);
		record = r;
	}

	public void addParams(List<Subtree> params) {
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

	public ParamSymbol paramType(Subtree nodeType, String sName, boolean [] pLocks) {
		SymbolType t;
		Symbol temp;
		int db = 373592855; //default size of an int 
		String symType = "";

		if(nodeType instanceof NaTypeDescriptor){
			nodeType = nodeType.children.get(0);

			if(nodeType instanceof BasicType){
				t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return (pLocks[2]) ? makeArray(sName, t, db, null, pLocks) : new ParamSymbol(sName, t, pLocks);
			}
			else if(nodeType instanceof Name){
				temp = resolve(nodeType.token.getName());

				if(temp == null || !temp.getTypeSymbol())
					System.out.println("param type undefined");
				else{
					symType = temp.getType().getTypeName();

					if(symType == "array")
						t = (ArraySymbol)temp.getType();
					else if(symType == "record")
						t = (RecordSymbol)temp.getType();
					else 
						t = (BuiltInTypeSymbol)temp.getType();

					return (pLocks[2]) ? makeArray(sName, t, db, null, pLocks) : 
						new ParamSymbol(sName, t, pLocks);
				}
			}
			else if(nodeType instanceof RecordDescriptor){
				RecordSymbol record = new RecordSymbol(sName, this, 
					nodeType.children.get(0).children);

				t = (RecordSymbol)enclosing.resolve(nodeType.token.getTokenType());
				return (pLocks[2]) ? makeArray(sName, t, db, record, pLocks) : 
					new ParamSymbol(sName, t, record, pLocks);
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

	public ParamSymbol makeArray(String n, SymbolType t, int s, RecordSymbol r, boolean [] l){
		ArraySymbol arr = (r == null) ? new ArraySymbol(n, s, t) : new ArraySymbol(n, s, t, r);
		SymbolType typ = (ArraySymbol)enclosing.resolve("array");

		return new ParamSymbol(n, typ, arr, l);
	}

}