import java.util.*;
import java.io.*;

public class RecordSymbol extends ScopedSymbol implements SymbolType, Scope{

	public RecordSymbol(String n, Scope enclosing){
		super(n, enclosing);
	}


	@Override
	public String getTypeName(){ return ""; }
}