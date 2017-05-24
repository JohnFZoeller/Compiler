import java.io.*;
import java.util.*;

public class Symbol {
	public String name;
	public SymbolType type;			//for built in types

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
	Boolean staticLock;			//var available outside of declared block
	Boolean constLock;			//var unchangeable
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

class FunctionSymbol extends Symbol {
	FunctionSymbol(String n, SymbolType t){
		super(n, t);
	}

}
