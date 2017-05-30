import java.util.*;
import java.io.*;

public class SymbolTable {

	GlobalScope globals = new GlobalScope();

	public SymbolTable() {
		initialize();
	}

	public void initialize() {
		globals.define(new BuiltInTypeSymbol("float64"));
		globals.define(new BuiltInTypeSymbol("byte"));
		globals.define(new BuiltInTypeSymbol("int32"));
		globals.define(new BuiltInTypeSymbol("void"));
		globals.define(new RecordSymbol("record", globals));
		globals.define(new ArraySymbol("array"));
	}

	public String toString() {
		return "";
		//still need to implement
	}

}


