import java.io.*;
import java.util.*;

public class Symbol {
	public String name;
	public SymbolType type;			//for built in types
	public boolean isVar = false;
	public boolean isTypeSymbol = false;

	Symbol(){
		name = "";
		type = null;
	}

	Symbol(String n, SymbolType t){
		name = n;
		type = t;
	}

	Symbol(String n){
		name = n;
		type = null;
	}

	public void setVar(){ isVar = true; }

	public boolean getVar(){ return isVar; }

	public void setTypeSymbol(){ isTypeSymbol = true; }

	public boolean getTypeSymbol() { return isTypeSymbol; }

	public String getName(){ return name; }

	public SymbolType getType() { return type; }

	public String toString(){
		if(type != null) 
			return '<' + getName() + ": " + type + '>';
		return getName();
	}

	public void addParams(List<Subtree> params) throws AlreadyDefinedException, UndefinedTypeException, IllegalOperationException {
		System.out.println("wrong one");
	}
}

class ExpressionSymbol extends Symbol{
	ExpressionSymbol(SymbolType t){
		super("", t);
	}
}

class ParamSymbol extends Symbol {
	public boolean [] locks; //[0] = ref, [1] = const
	RecordSymbol record;
	ArraySymbol array;

	ParamSymbol(String n, SymbolType t, boolean [] l){
		super(n, t);
		locks = l;
	}

	ParamSymbol(String n, SymbolType t, RecordSymbol r, boolean [] l){
		super(n, t);
		locks = l;
		record = r;
	}

	ParamSymbol(String n, SymbolType t, ArraySymbol a, boolean [] l){
		super(n, t);
		locks = l;
		array = a;
	}
}

class VarSymbol extends Symbol {
	RecordSymbol record;
	boolean [] locks; //[0] = static, [1] = const;
	ArraySymbol array;

	VarSymbol(String n, SymbolType t){
		super(n, t);
		locks = null;
		setVar();
	}

	VarSymbol(String n, SymbolType t, boolean[] l){
		super(n, t);
		locks = l;
		setVar();
	}

	VarSymbol(String n, SymbolType t, RecordSymbol r, boolean[] l){
		super(n, t);
		locks = l;
		record = r;
		setVar();
	}

	VarSymbol(String n, SymbolType t, boolean [] l, ArraySymbol a){
		super(n, t);
		locks = l;
		array = a;
		setVar();
	}
}

class TypeSymbol extends Symbol {
	RecordSymbol record;
	ArraySymbol array;

	//add optional dimension member
	TypeSymbol(String n, SymbolType t) {
		super(n, t);
		setTypeSymbol();
	}

	TypeSymbol(String n, SymbolType t, RecordSymbol r){
		super(n, t);
		record = r;
		setTypeSymbol();
	}

	TypeSymbol(String n, SymbolType t, ArraySymbol a){
		super(n, t);
		setTypeSymbol();
		array = a;
	}
}
