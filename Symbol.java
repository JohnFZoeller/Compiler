import java.io.*;
import java.util.*;

public class Symbol {
	public String name;
	public SymbolType type;			//for built in types
	public boolean isVar = false;

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

	public String getName(){ return name; }

	public SymbolType getType() { return type; }

	public String toString(){
		if(type != null) 
			return '<' + getName() + ": " + type + '>';
		return getName();
	}

	public void addParams(List<Subtree> params) throws AlreadyDefinedException, UndefinedTypeException {
		System.out.println("wrong one");
	}
}

class RefConstSymbol extends Symbol {
	RefConstSymbol() {
		super();
	}

	RefConstSymbol(String n, SymbolType t) {
		super(n, t);
	}

	RefConstSymbol(String n) {
		super(n);
	}
}

class ConstSymbol extends Symbol {
	ConstSymbol() {
		super();
	}

	ConstSymbol(String n, SymbolType t) {
		super(n, t);
	}

	ConstSymbol(String n) {
		super(n);
	}
}

class RefSymbol extends Symbol {
	RefSymbol() {
		super();
	}
	
	RefSymbol(String n, SymbolType t) {
		super(n, t);
	}

	RefSymbol(String n) {
		super(n);
	}
}

class ExpressionSymbol extends Symbol{
	ExpressionSymbol(SymbolType t){
		super("", t);
	}
}

class VarSymbol extends Symbol {
	RecordSymbol record;
	boolean [] locks; //[0] = static, [1] = const;
	//add optional dimension member

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
}

class TypeSymbol extends Symbol {
	RecordSymbol record;

	//add optional dimension member
	TypeSymbol(String n, SymbolType t) {
		super(n, t);
	}

	TypeSymbol(String n, SymbolType t, RecordSymbol r){
		super(n, t);
		record = r;
	}
}
