import java.util.*;
import java.io.*;

//symbol table should no longer implement Scope
//page 141 infers this was only in the case of a monolithic scope

public class SymbolTable { //implements Scope

	GlobalScope globals = new GlobalScope();

	public SymbolTable() {
		initialize();
	}

	public void initialize() {
		//define base types and add them to global
		globals.define(new BuiltInTypeSymbol("float64"));
		globals.define(new BuiltInTypeSymbol("byte"));
		globals.define(new BuiltInTypeSymbol("int32"));
		globals.define(new BuiltInTypeSymbol("void"));
		globals.define(new RecordSymbol("record", globals));
	}

	public String toString() {
		return "";
		//still need to implement
	}

}


