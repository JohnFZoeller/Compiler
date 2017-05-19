import java.util.*;
import java.io.*;

public class BaseScope implements Scope {
	Map<String, Symbol> syms = new HashMap<String, Symbol>();
	SymbolTable enclosingScope;

	public BaseScope(){ 
		enclosingScope = null;
	}

	public BaseScope(Scope enclosing){
		enclosingScope = enclosing;
	}


	//public void add(String id, Type binding) {}

	//think this will likely need to be redone
	public String toString() { return getScopeName() + ": " + syms; }

	/***************************Scope Interface*************************/

	public String getScopeName(){ return ""; }

	public SymbolTable getEnclosingScope(){ return enclosingScope; }

	public void define(Symbol sym){ 
		syms.put(sym.name, sym); 
		sym.scope = this;			//ensure that the scope is
	}

	public Symbol resolve(String name){
		Symbol temp = syms.get(name);

		if(temp != null) return temp;

		if(enclosingScope != null)
			return enclosingScope.resolve(name);

		return null;
	}

}


