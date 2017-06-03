/*=============================== BaseScope ===================================

/**	BaseSopes. Lays groundwork for local and global scopes for the abstract
 *	syntax tree and semantic analysis. BaseScope object will hold all of the
 *	symbols within the scope. This can be used for the semantic analysis and
 *	code generation steps of compiling. BaseScope allows for nested scoping
 *	with the Scope enclosing data member cotaining a reference to the outer
 *	Scope object's symbols. Data member syms holds all of the symbols for
 *	the current Scope.
 *
 *	@author		John Zoeller
 *	@author		Destiny Boyer	
 *	@see 		Scope
 *	@see 		LocalScope
 *	@see		GlobalScope
 */

import java.util.*;
import java.io.*;

public class BaseScope implements Scope {

	//allows for nesting
	Scope enclosingScope;
	//holds all symbols within the current scope
	Map<String, Symbol> syms = new HashMap<String, Symbol>();

	/*=========================== Constructors ==============================*/

	/**
	 *	Constructor initializes a new scope with no outer scope and an
	 *	empty symbol table.
	 *	
	 *	@see 		Scope
	 */

	public BaseScope(){ 
		enclosingScope = null;
	}

	/**
	 *	Constructor used for instances where BaseScope is nested inside
	 *	another Scope object. enclosingScope is set to reference
	 *	enclosing.
	 *	
	 *	@param	enclosing	reference to the scope enclosing this
	 *	@see 		Symbol
	 */

	public BaseScope(Scope enclosing){
		enclosingScope = enclosing;
	}

	/**
	 *	Prints the name scope name for this and ": " for easy debugging and
	 *	printing of symbol tables.
	 *	
	 *	@param	n	string for name to be set to.
	 *	@see 		BaseScope#getScopeName()
	 */

	public String toString(){
		return getScopeName() + ": ";
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

