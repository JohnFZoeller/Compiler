import java.util.*;
import java.io.*;

public class ArraySymbol extends Symbol implements SymbolType {
	int size;
	String typeName;

	ArraySymbol(String n, int s, SymbolType t){
		super(n, t);
		size = s;
	}

	@Override
	public String getName(){ return name; }	

	public String getTypeName(){ return typeName; }

}