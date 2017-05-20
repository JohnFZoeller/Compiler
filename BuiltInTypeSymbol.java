import java.io.*;
import java.util.*;

public class BuiltInTypeSymbol extends Symbol implements SymbolType {
	BuiltInTypeSymbol(String n){
		super(n);
	}

	@Override
	public String getName(){ return "BuiltInTypeSymbol"; }

	public String getTypeName(){ return "BuiltInTypeSymbol"; }
	
}