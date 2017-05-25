import java.util.*;
import java.io.*;

class FunctionSymbol extends ScopedSymbol implements Scope {
	FunctionSymbol(String n, SymbolType t, Scope e){
		super(n, t, e);
	}

}