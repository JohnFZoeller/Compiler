import java.util.*;
import java.io.*;

public class RecordSymbol extends ScopedSymbol implements SymbolType, Scope{

	public RecordSymbol(String n, Scope enclosing){
		super(n, enclosing);
	}

	public RecordSymbol(String n, Scope e, List<Subtree> fieldDecls){
		super(n, e);
		addDecls(fieldDecls);
	}

	public void addDecls(List<Subtree> fds){
		for(int i = 0; i < fds.size(); i++){
			Subtree nodeType = fds.get(i).children.get(1).children.get(0).children.get(0);
 			define(fieldDeclType(nodeType, fds.get(i).token.getName()));
		}
	}

	public Symbol fieldDeclType(Subtree nodeType, String sName){
		SymbolType t;
		Symbol temp;

		if(nodeType instanceof BasicType){
			t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
			return new Symbol(sName, t);
		}
		else if(nodeType instanceof Name){
			temp = resolve(sName);

			if(temp == null)
				System.out.println("record member undefined");
			else{
				t = (BuiltInTypeSymbol)temp.getType();
				return new Symbol(sName, t);
			}
		}
		else if(nodeType instanceof RecordDescriptor){
			RecordSymbol record = new RecordSymbol(sName, this, 
				nodeType.children.get(0).children);

			t = (RecordSymbol)enclosing.resolve(nodeType.token.getTokenType());
			return new VarSymbol(sName, t, record, null);
		}

		System.out.println("record parse error");
		return null;
	}

	@Override
	public String getTypeName(){ return ""; }
}