import java.util.*;
import java.io.*;

public class BaseScope implements Scope {
	Map<String, Symbol> syms = new HashMap<String, Symbol>();
	Scope enclosingScope;

	public BaseScope(){ 
		enclosingScope = null;
		//collapse
	}

	public BaseScope(Scope enclosing){
		enclosingScope = enclosing;
		//collapse
	}

	public String toString(){
		return getScopeName() + ": ";
		//collapse
	}

	//public void add(String id, Type binding) {}

	/***************************Scope Interface*************************/

	public String getScopeName(){
		return ""; 
		//collapse
	}

	public Scope getEnclosingScope(){
		return enclosingScope;
		//collapse
	}

	public void define(Symbol sym){ 
		syms.put(sym.name, sym); 
		//sym.scope = this;			//this only would have been 
		//applicable if we were using classes, you would have had to
		//search back up through class hierarchies via old scopes
		//to resolve whether or not a something could access a base class
		//member.   No classes tho so nada
	}

	public Symbol resolve(String name){
		Symbol temp = syms.get(name);

		if(temp != null) return temp;

		if(enclosingScope != null)
			return enclosingScope.resolve(name);

		return null;
	}

}

