import java.util.*;
import java.io.*;

class FunctionSymbol extends ScopedSymbol implements Scope {
	FunctionSymbol(String n, SymbolType t, Scope e){
		super(n, t, e);
	}

	FunctionSymbol(String n, SymbolType t, Scope e, List<Subtree> p){
		super(n, t, e);
		addParams(p);
	}

	public void addParams(List<Subtree> params){
		if(params == null) return;
		
		for(int i = 0; i < params.size(); i++){
			System.out.println(params.get(i).children.get(0).token.getName());
		}

	}

}