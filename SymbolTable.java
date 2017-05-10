import java.util.*;
import java.io.*;

public class SymbolTable implements Scope{
	Map<String, Symbol> syms = new HashMap<String, Symbol>();

	public SymbolTable(){ initTypeSystem(); }

	protected void initTypeSystem(){
		define(new BuiltInTypeSymbol("int32"));
		define(new BuiltInTypeSymbol("float64"));
	}

	public String toString() { return getScopeName() + ": " + syms; }

	/***************************Scope Interface*************************/

	public String getScopeName(){ return "global"; }

	public Scope getEnclosingScope(){ return null; }

	public void define(Symbol sym){ syms.put(sym.name, sym); }

	public Symbol resolve(String name){ return syms.get(name); }
}