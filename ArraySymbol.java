import java.util.*;
import java.io.*;

public class ArraySymbol extends Symbol implements SymbolType {
	int size;
	String typeName;
	RecordSymbol record;
	List<Symbol> array = new ArrayList<Symbol>();

	ArraySymbol(String n, int s, SymbolType t, RecordSymbol r){
		super(n, t);
		size = s;
		record = r;
		createArray();
	}

	ArraySymbol(String n, int s, SymbolType t){
		super(n, t);
		size = s;
		createArray();
	}

	ArraySymbol(String n){
		super(n);
		createArray();
	}

	void setSize(int s) { size = s; }

	int getSize() { return size; }

	public void createArray(){ // 0xDEADBEEF -> 3735928559
		if(size == 373592855){
			return;
		}

		for(int i = 0; i < size; i++){
			array.add(new Symbol(name, type)); 
		}
	}

	public Symbol getIndex(int i){
		return array.get(i);
	}

	@Override
	public String getName(){ return name; }	

	public String getTypeName(){ return "array"; }

}