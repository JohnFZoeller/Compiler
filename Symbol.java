import java.io.*;
import java.util.*;

public class Symbol{
	public String name;
	public Type type;

	Symbol(String n, Type t){
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
	VarSymbol(String n, Type t){
		super(n, t);
	}
}
