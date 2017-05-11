import java.util.*;
import java.io.*;

public class ScopeTree{
	public List<ScopeTree> childs;
	public ScopeTree parent;
	public SymbolTable table;

	//root constructor
	public ScopeTree(){
		parent = null;
		table = new SymbolTable();
	}

	//children constructor
	public ScopeTree(ScopeTree parentNode){
		parent = parentNode;
		table = new SymbolTable(parentNode.table);
	}

	public void addChild(ScopeTree s){
		if(childs == null) childs = new ArrayList<ScopeTree>();

		childs.add(s);
	}
}