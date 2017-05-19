import java.io.*;
import java.util.*;

public class Symbol{
	public String name;
	public TypeInterface type;

	Symbol(String n, TypeInterface t){
		name = n;
		type = t;
	}

	Symbol(String n){
		name = n;

	}

	public String getName(){ return name; }

	public String toString(){
		if(type != null) 
			return '<' + getName() + ": " + type + '>';
		return getName();
	}
}

class VarSymbol extends Symbol{
	VarSymbol(String n, TypeInterface t){
		super(n, t);
	}
}

class FuncSymbol extends Symbol implements Scope {
	FuncSymbol(String n, TypeInterface t){
		super(n, t);
	}

	public String getScopeName(){return "";}
	public Scope getEnclosingScope(){return null;}
	public void define(Symbol sym){;}
	public Symbol resolve(String name){return null;}
}
