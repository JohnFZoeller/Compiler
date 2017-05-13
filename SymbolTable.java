import java.util.*;
import java.io.*;

public class SymbolTable implements Scope{
	Map<String, Symbol> syms = new HashMap<String, Symbol>();
	SymbolTable enclosingScope;

	public SymbolTable(){ 
		enclosingScope = null;
	}

	public SymbolTable(SymbolTable parent){
		enclosingScope = parent;
	}

	protected void initTypeSystem(){
		//not sure if we'll actually need these
		define(new BuiltInTypeSymbol("float64"));
		define(new BuiltInTypeSymbol("byte"));
	}

	public String toString() { return getScopeName() + ": " + syms; }

	/***************************Scope Interface*************************/

	public String getScopeName(){ return ""; }

	public SymbolTable getEnclosingScope(){ return enclosingScope; }

	public void define(Symbol sym){ 
		syms.put(sym.name, sym); 
	}

	public Symbol resolve(String name){
		Symbol temp = syms.get(name);

		if(temp != null) return temp;

		if(enclosingScope != null)
			return enclosingScope.resolve(name);

		return null;
	}
}


