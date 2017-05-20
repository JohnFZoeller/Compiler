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

	public String toString(){
		if(type != null) 
			return '<' + getName() + ": " + type + '>';
		return getName();
	}
}

class VarSymbol extends Symbol{
	VarSymbol(String n, SymbolType t){
		super(n, t);
	}

	VarSymbol(String n, Symbol s){
		super(n, s);
	}
}

class TypeSymbol extends Symbol{
	TypeSymbol(String n, SymbolType t){
		super(n, t);
	}

	// TypeSymbol(String n, Symbol s){
	// 	super(n, s);
	// }
}

class FunctionSymbol extends Symbol {
	FunctionSymbol(String n, SymbolType t){
		super(n, t);
	}

}
