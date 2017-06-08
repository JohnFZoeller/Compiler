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
			Subtree nodeType = fds.get(i).children.get(1);
			Symbol temp = fieldDeclType(nodeType, fds.get(i).children.get(0).token.getName());
 			define(temp);
		}
	}

	public Symbol fieldDeclType(Subtree nodeType, String sName){
		SymbolType t;
		Symbol temp;
		boolean isArray = ((TypeDescriptor)nodeType).array;
		String symType = "";
		nodeType = nodeType.children.get(0).children.get(0);

		if(nodeType instanceof BasicType){
			t = (BuiltInTypeSymbol)enclosing.resolve(nodeType.token.getTokenType());
			//getSize
			return (isArray) ? makeArray(sName, t, 4, null) : new Symbol(sName, t);
		}
		else if(nodeType instanceof Name){
			temp = resolve(nodeType.token.getName());

			if(temp == null || !temp.getTypeSymbol())
				System.out.println("record member undefined");
			else{
				symType = temp.getType().getTypeName();

				if(symType == "array")
					t = (ArraySymbol)temp.getType();
				else if(symType == "record")
					t = (RecordSymbol)temp.getType();
				else 
					t = (BuiltInTypeSymbol)temp.getType();

				return (isArray) ? makeArray(sName, t, 4, null) : new Symbol(sName, t);
			}
		}
		else if(nodeType instanceof RecordDescriptor){
			RecordSymbol record = new RecordSymbol(sName, this, 
				nodeType.children.get(0).children);

			t = (RecordSymbol)enclosing.resolve("record");

			return (isArray) ? makeArray(sName, t, 4, record) : new VarSymbol(sName, t, record, null);
		}

		System.out.println("record parse error");
		return null;
	}

	public Symbol makeArray(String n, SymbolType t, int s, RecordSymbol r){
		ArraySymbol arr = (r == null) ? new ArraySymbol(n, s, t) : new ArraySymbol(n, s, t, r);
		SymbolType typ = (ArraySymbol)enclosing.resolve("array");

		return new VarSymbol(n, typ, null, arr);
	}

	//context:  var john record a int32 end;
	//john.a = 3;
	//reference john; call getDecl("a"); 
	//will reference "a" and return its symbol object
	public Symbol getDecl(String declName){
		return resolve(declName);
	}

	public void saveConstValues(List<String> consts, String prefix){
		String instruction = (prefix == null) ? "rec_" + getName() + "_" 
			:  prefix + "rec_" + getName() + "_";

		String symtype;

		for (Map.Entry<String, Symbol> entry : members.entrySet()) {
    		Symbol value = entry.getValue();
    		String typeName = value.getType().getTypeName();

    		instruction += (typeName == "record") ? "rec_" + entry.getKey() + ":\n\t"
    			: entry.getKey() + ":\n\t";

			if(typeName == "int32" || typeName == "byte"){
				instruction += (typeName == "int32") ? "int_literal " + defaultInt 
				: "int_literal " + defaultByte;
			} 
			else if(typeName == "float64"){
				instruction += "float_literal " + defaultFloat;
			}
			else if(typeName == "array"){
				typeName = ((VarSymbol)value).array.getType().getTypeName();	//arrayType

				if(typeName == "int32" || typeName == "byte"){
					instruction += "int_literal ";								//print array name
					instruction += (typeName == "int32") ? defaultIntArray : defaultByteArray;
					consts.add(instruction);									//add array name

					instruction = "rec_" + getName() + "_" + entry.getKey() 				//print array size
						+ "_size: \n\tint_literal " 
						+ Integer.toString(((VarSymbol)value).array.getSize());
																				//print array alloc
					System.out.println("load_label " + "rec_" + getName() + "_" 
						+ entry.getKey() + "_size\nload_mem_int \nalloc_int");
				}
				else if(typeName == "float64"){
					instruction += "float_literal " + defaultFloatArray;		
					consts.add(instruction);

					instruction = "rec_" + getName() + "_" + entry.getKey()		
						+ "_size: \n\tint_literal " 
						+ Integer.toString(((VarSymbol)value).array.getSize());

					System.out.println("load_label " + "rec_" + getName() + "_" 
						+ entry.getKey() + "_size\nload_mem_int \nalloc_float");
				}
				else if(typeName == "record"){
					instruction += "int_literal " + defaultRecordArray;		
					consts.add(instruction);

					instruction = "rec_" + getName() + "_" + entry.getKey()
						+ "_size: \n\tint_literal " 
						+ Integer.toString(((VarSymbol)value).array.getSize());

					System.out.println("load_label " + "rec_" + getName() + "_" 
						+ entry.getKey() + "_size\nload_mem_int \nalloc_int");
				}
			}
			else if(typeName == "record"){
				RecordSymbol tempRecord = ((VarSymbol)value).record;
				instruction += "int_literal " + defaultRecord;
				tempRecord.saveConstValues(consts, "rec_" + getName() + "_");
			}
			consts.add(instruction);
			instruction = "rec_" + getName() + "_";
		}


	}

	@Override
	public String getTypeName(){ return "record"; }
}