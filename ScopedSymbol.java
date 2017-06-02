import java.util.*;
import java.io.*;

public abstract class ScopedSymbol extends Symbol implements Scope{
	Scope enclosing;
	Map<String, Symbol> members = new LinkedHashMap<String, Symbol>();

	public ScopedSymbol(String n, SymbolType t, Scope enclose){
		super(n, t);
		enclosing = enclose;
	}

	public ScopedSymbol(String n, Scope enclose){
		super(n);
		enclosing = enclose;
	}

	public Map<String, Symbol> getMembers(){
		return members;
	}

	public Symbol resolveMember(String name){
		return members.get(name);
	}

	public String getScopeName(){
		return name;
	}

	public Scope getEnclosingScope(){
		return enclosing;
	}
	public void define(Symbol sym){
		if(sym == null) System.out.println("ScopedSymbol define error");
		
		getMembers().put(sym.name, sym);
	}

	public Symbol resolve(String name){
		Symbol temp = getMembers().get(name);

		if(temp != null)
			return temp;

		if(getEnclosingScope() != null)
			return getEnclosingScope().resolve(name);

		return null;
	}
}