import java.io.*;
import java.util.*;

public class BuiltInTypeSymbol extends Symbol implements SymbolType {
	String typeName;
	BuiltInTypeSymbol(String n){
		super(n);
		typeName = n;
	}

	@Override
	public String getName(){ return name; }

	public String getTypeName(){ return typeName; }
	
}