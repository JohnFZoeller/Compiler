import java.io.*;
import java.util.*;

public class Symbol {
	public String name;
	public SymbolType type;			//for built in types
	public Symbol symbol;			//for user defined types

	Symbol(){
		name = "";
		type = null;
		symbol = null;
	}

	Symbol(String n, Symbol s){
	 	name = n;
	 	symbol = s;
	 	type = null;
	}

	Symbol(String n, SymbolType t){
		name = n;
		type = t;
		symbol = null;
	}

	Symbol(String n){
		name = n;
		type = null;
		symbol = null;
	}

	public String getName(){ return name; }

	public SymbolType getType() { return type; }

	public String toString(){
		if(type != null) 
			return '<' + getName() + ": " + type + '>';
		return getName();
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

class VarSymbol extends Symbol {
	RecordSymbol record;
	//add optional dimension member

	VarSymbol(String n, SymbolType t){
		super(n, t);
	}

	VarSymbol(String n, SymbolType t, RecordSymbol r){
		super(n, t);
		record = r;
	}

	//commented out because this caused a compile erro
}

class TypeSymbol extends Symbol {
	//add optional dimension member
	TypeSymbol(String n, SymbolType t) {
		super(n, t);
	}

}

class FunctionSymbol extends Symbol {
	FunctionSymbol(String n, SymbolType t){
		super(n, t);
	}

}
