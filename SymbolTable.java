import java.util.*;
import java.io.*;

public class SymbolTable implements Scope {

	GlobalScope globals = new GlobalScope();

	public SymbolTable() {
		initialize();
	}

	public void initialize() {
		//define base types and add them to global
		globals.define(new BuiltInType("float64"));
		globals.define(new BuiltInType("byte"));
		globals.define(new BuiltInType("int32"));
	}

	public String toString() {
		//still need to implement
	}

}


